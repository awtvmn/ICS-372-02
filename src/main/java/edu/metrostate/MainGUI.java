package edu.metrostate;
import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * MainGUI class - Feature 6
 * Users can click buttons.
 */
public class MainGUI extends Application {

    private OrderManager orderManager;
    private Directory directory;

    // list of orders on the left
    private ObservableList<Order> orderListItems = FXCollections.observableArrayList();
    private ListView<Order> orderListView = new ListView<>(orderListItems);

    // bottom text
    private TextFlow outputArea = new TextFlow();

    /**
     * init - if program has loaded before, loads progress from before
     * feature 2
     */
    @Override
    public void init() {
        orderManager = new OrderManager(); //moved from start to have only one instance
        orderManager.loadOrders(); //retrieves any previous data
    }

    /**
     * stop - when program gets stopped, saves info for next time
     * feature 2
     */
    @Override
    public void stop() {
        orderManager.saveOrder(); //saves any new data
    }

    /**
     * start - runs when the app launches, builds the window
     */
    @Override
    public void start(Stage primaryStage) {
        // start the directory watcher (Feature 5)
        // checks the watched/ folder every 3 seconds for new files
        directory = new Directory(orderManager);
        directory.setOnOrderImported(this::refreshOrderList);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> directory.checkFolder()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        orderListView.setCellFactory(listView -> new ListCell<>(){
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);
                if (order == null || empty) {
                    setText(null);
                    setStyle("");
                    setGraphic(null);
                    return;
                }
                String icon;

                if (order.getOrderStatus() == OrderStatus.COMPLETED) {
                    icon = "✅";
                } else if (order.getType().equalsIgnoreCase("delivery")) {
                    icon = "🚗";
                } else if (order.getType().equalsIgnoreCase("ship")) {
                    icon = "🛫";
                } else {
                    icon = "🛒";
                }

                setText(icon + "Order #" + order.getOrderID()
                        + " | " + order.getType()
                        + " | " + order.getOrderStatus());

                String color;
                if(order.getOrderStatus() == OrderStatus.CANCELED){
                    color = "gray";
                } else if(order.getOrderStatus() == OrderStatus.COMPLETED){
                    color = "green";
                } else if (order.getType().equalsIgnoreCase("delivery")){
                    color = "red";
                } else if (order.getType().equalsIgnoreCase("ship")){
                    color = "blue";
                } else {
                    color = "orange";
                }

                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            }
        });

        // left side - order list
        orderListView.setPrefWidth(300);
        VBox leftPanel = new VBox(5, new Label("Orders:"), orderListView);

        // right side - buttons
        Button btnStart      = new Button("Start Order");
        Button btnComplete   = new Button("Complete Order");
        Button btnDisplay    = new Button("Display Order");
        Button btnCancel     = new Button("Cancel Order");
        Button btnUncomplete = new Button("Show Uncompleted Orders");
        Button btnExport     = new Button("Export All Orders");

        // make all buttons same width
        for (Button b : new Button[]{btnStart, btnComplete,
                btnDisplay, btnCancel, btnUncomplete, btnExport}) {
            b.setMaxWidth(Double.MAX_VALUE);
        }



        VBox buttonPanel = new VBox(8,
                new Label("Actions:"),
                btnStart,
                btnComplete,
                btnDisplay,
                btnCancel,
                new Separator(),
                btnUncomplete,
                btnExport,
                new Separator()
        );
        buttonPanel.setPrefWidth(180);
        buttonPanel.setPadding(new Insets(0, 0, 0, 10));



        // bottom - output messages
        ScrollPane scrollPane = new ScrollPane(outputArea);
        scrollPane.setPrefHeight(150);
        scrollPane.setFitToWidth(true);
        // auto scroll to bottom when new text is added
        outputArea.heightProperty().addListener((obs, oldVal, newVal) ->
                scrollPane.setVvalue(1.0));

        // connect buttons to their actions
        btnStart.setOnAction(e -> handleAction("start"));
        btnComplete.setOnAction(e -> handleAction("complete"));
        btnDisplay.setOnAction(e -> handleAction("display"));
        btnCancel.setOnAction(e -> handleAction("cancel"));
        btnUncomplete.setOnAction(e -> handleShowUncompleted());
        btnExport.setOnAction(e -> handleExport());

        // put it all together
        HBox centerPanel = new HBox(leftPanel, buttonPanel);
        VBox root = new VBox(10, centerPanel, new Label("Output:"), scrollPane);
        root.setPadding(new Insets(15));

        // show the window
        primaryStage.setTitle("Warehouse Order System");
        primaryStage.setScene(new Scene(root, 520, 500));
        primaryStage.show();

        refreshOrderList();
        log("System ready. Please drag and drop your order file in \"watched\" folder.\nSystem will import any new files added into the folder every 3 seconds.");
    }

    /**
     * Start, Complete, Cancel, and Display buttons
     * User must select an order from the list first
     */
    private void handleAction(String action) {
        Order selected = orderListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log("Please select an order from the list first.");
            return;
        }

        // get the order ID from the selected list item
        int orderID = selected.getOrderID();

        // perform the action
        if (action.equals("start")) {
            OrderStatus statusBefore = orderManager.getAllOrders().get(orderID).getOrderStatus();
            orderManager.startOrder(orderID);
            if(statusBefore == OrderStatus.CANCELED) {
                log("Order #" + orderID + " cannot be started. It has been canceled.");
            } else if (statusBefore == OrderStatus.IN_PROGRESS) {
                log("Order #" + orderID + " is already started.", Color.RED);
            } else if (statusBefore == OrderStatus.COMPLETED) {
                log("Order #" + orderID + " is already completed.", Color.RED);
            } else {
                log("Started order #" + orderID, Color.BLUE);
            }

        } else if (action.equals("complete")) {
            OrderStatus statusBefore = orderManager.getAllOrders().get(orderID).getOrderStatus();
            orderManager.completeOrder(orderID);
            if(statusBefore == OrderStatus.CANCELED) {
                log("Order #" + orderID + " has been canceled. Cancelled orders cannot be completed.");
            } else if (statusBefore == OrderStatus.COMPLETED) {
                log("Order #" + orderID + " is already completed.", Color.RED);
            } else if (statusBefore == OrderStatus.INCOMING) {
                log("Order #" + orderID + " cannot be completed. Must be started first.", Color.RED);
            } else {
                log("Completed order #" + orderID, Color.GREEN);
            }

        } else if (action.equals("display")) {
            Order order = orderManager.getAllOrders().get(orderID);
            if(order == null){
                log("Order not found.");
                return;
            } else if (order.getOrderStatus() == OrderStatus.CANCELED) {
                log("Canceled orders cannot be displayed.");
                return;
            }

            String typeIcon;
            if(order.getType().equalsIgnoreCase("delivery")){
                typeIcon = "🚗";
            } else if (order.getType().equalsIgnoreCase("ship")){
                typeIcon = "🛫";
            } else {
                typeIcon = "🛒";
            }


            log("---Order Details---");
            log("Order ID: " + order.getOrderID());
            log("Order Date: " + order.getOrderDate());
            log("Order Status: " + order.getOrderStatus(),
                    order.getOrderStatus() == OrderStatus.COMPLETED ? Color.GREEN : Color.PURPLE);
            log(typeIcon + "Order Type: " + order.getType(),
                    order.getType().equalsIgnoreCase("delivery") ? Color.RED :
                    order.getType().equalsIgnoreCase("ship") ? Color.BLUE : Color.ORANGE);

            log("Items: ");
            for(Item item : order.getItems()){
                log("\t" + item.getName()
                        + "(Quantity: " + item.getQuantity()
                        + ", Price: $" + item.getPrice() + ")");
            }
        } else if (action.equals("cancel")) { //feature 1
            OrderStatus statusBefore = orderManager.getAllOrders().get(orderID).getOrderStatus();
            if(statusBefore == OrderStatus.COMPLETED) {
                log("Order #" + orderID + " has been completed and can no longer be canceled.");
                return;
            } else if (statusBefore == OrderStatus.CANCELED) {
                log("Order #" + orderID + " has already been canceled.");
            } else {
                TextInputDialog dialog = new TextInputDialog("...");
                dialog.setTitle("Cancel order?");
                dialog.setHeaderText("Please enter reason to cancel order.");
                dialog.setContentText("Reason:");
                Optional<String> reason = dialog.showAndWait();

                // Handle the reason
                if (reason.isPresent() && !reason.get().trim().isEmpty()) {
                    orderManager.cancelOrder(orderID);
                    if (statusBefore == OrderStatus.IN_PROGRESS) {
                        log("Order # " + orderID + " has stopped being fulfilled and been canceled. Reason: " + reason.get());
                    } else {
                        log("Order # " + orderID + " is canceled. Reason: " + reason.get());

                    }

                } else {
                    log("No reason entered. Order has not been canceled");
                }
            }


        }

        refreshOrderList();
    }

    /**
     * Shows all uncompleted orders
     */
    private void handleShowUncompleted() {
        log("--- Uncompleted Orders ---");
        boolean found = false;
        for (Map.Entry<Integer, Order> entry : orderManager.getAllOrders().entrySet()) {
            Order order = entry.getValue();
            if (order.getOrderStatus() != OrderStatus.COMPLETED) {
                log("Order #" + order.getOrderID() + " | " + order.getType()
                        + " | " + order.getOrderStatus());
                found = true;
            }
        }
        if (!found) log("No uncompleted orders.");
    }

    /**
     * Exports all orders to a file
     */
    private void handleExport() {
        orderManager.exportOrders();
        log("Orders exported successfully.\nAll exported orders are in exported json files");
    }

    /**
     * Updates the order list on the left side of the window
     */
    private void refreshOrderList() {
        orderListItems.clear();
        for (Map.Entry<Integer, Order> entry : orderManager.getAllOrders().entrySet()) {
            orderListItems.add(entry.getValue());
        }
    }


    /**
     * Prints a message to the output area at the bottom
     */
    private void log(String message, Color color) {
        Text text = new Text(message + "\n");
        text.setFill(color);
        outputArea.getChildren().add(text);
    }

    private void log(String message){
        log(message, Color.BLACK);
    }

    /**
     * Starts the JavaFX app
     */
    public static void main(String[] args) {
        launch(args);
    }
}