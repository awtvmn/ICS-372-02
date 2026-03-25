package edu.metrostate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.util.Map;

/**
 * MainGUI class - Feature 6
 * Users can click buttons.
 */
public class MainGUI extends Application {

    private OrderManager orderManager;
    private Directory directory;

    // list of orders on the left
    private ObservableList<String> orderListItems = FXCollections.observableArrayList();
    private ListView<String> orderListView = new ListView<>(orderListItems);

    // bottom text
    private TextArea outputArea = new TextArea();

    /**
     * start - runs when the app launches, builds the window
     */
    @Override
    public void start(Stage primaryStage) {

        // create the order manager
        orderManager = new OrderManager();

        // start the directory watcher (Feature 5)
        // checks the watched/ folder every 3 seconds for new files
        directory = new Directory(orderManager);
        directory.setOnOrderImported(this::refreshOrderList);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> directory.checkFolder()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // left side - order list
        orderListView.setPrefWidth(300);
        VBox leftPanel = new VBox(5, new Label("Orders:"), orderListView);

        // right side - buttons
        Button btnStart      = new Button("Start Order");
        Button btnComplete   = new Button("Complete Order");
        Button btnDisplay    = new Button("Display Order");
        Button btnUncomplete = new Button("Show Uncompleted Orders");
        Button btnExport     = new Button("Export All Orders");

        // make all buttons same width
        for (Button b : new Button[]{btnStart, btnComplete,
                btnDisplay, btnUncomplete, btnExport}) {
            b.setMaxWidth(Double.MAX_VALUE);
        }

        VBox buttonPanel = new VBox(8,
                new Label("Actions:"),
                btnStart,
                btnComplete,
                btnDisplay,
                new Separator(),
                btnUncomplete,
                btnExport
        );
        buttonPanel.setPrefWidth(180);
        buttonPanel.setPadding(new Insets(0, 0, 0, 10));

        // bottom - output messages
        outputArea.setEditable(false);
        outputArea.setPrefHeight(150);

        // connect buttons to their actions
        btnStart.setOnAction(e -> handleAction("start"));
        btnComplete.setOnAction(e -> handleAction("complete"));
        btnDisplay.setOnAction(e -> handleAction("display"));
        btnUncomplete.setOnAction(e -> handleShowUncompleted());
        btnExport.setOnAction(e -> handleExport());

        // put it all together
        HBox centerPanel = new HBox(leftPanel, buttonPanel);
        VBox root = new VBox(10, centerPanel, new Label("Output:"), outputArea);
        root.setPadding(new Insets(15));

        // show the window
        primaryStage.setTitle("Warehouse Order System");
        primaryStage.setScene(new Scene(root, 520, 500));
        primaryStage.show();

        refreshOrderList();
        log("System ready. Please create a new file in txt folder and put your orders in.\nYou can move your orders from txt folder to watched folder while app is running or before.");
    }

    /**
     * Start, Complete, Cancel, and Display buttons
     * User must select an order from the list first
     */
    private void handleAction(String action) {
        String selected = orderListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log("Please select an order from the list first.");
            return;
        }

        // get the order ID from the selected list item
        int orderID;
        try {
            orderID = Integer.parseInt(selected.split("#")[1].split(" ")[0]);
        } catch (Exception e) {
            log("Could not read order ID.");
            return;
        }

        // perform the action
        if (action.equals("start")) {
            orderManager.startOrder(orderID);
            log("Started order #" + orderID);

        } else if (action.equals("complete")) {
            orderManager.completeOrder(orderID);
            log("Completed order #" + orderID);

        } else if (action.equals("display")) {
            orderManager.displayOrder(orderID);
            log("Displayed order #" + orderID);
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
            Order order = entry.getValue();
            orderListItems.add("Order #" + order.getOrderID()
                    + " | " + order.getType()
                    + " | " + order.getOrderStatus());
        }
    }

    /**
     * Prints a message to the output area at the bottom
     */
    private void log(String message) {
        outputArea.appendText(message + "\n");
    }

    /**
     * Starts the JavaFX app
     */
    public static void main(String[] args) {
        launch(args);
    }
}