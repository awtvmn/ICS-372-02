package edu.metrostate;
import javafx.application.Application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import java.util.Map;
import java.util.Optional;
import java.util.List;


/**
 * MainGUI class - Feature 6
 * Provides a graphical interface for the warehouse order system.
 *
 * Layout overview:
 * - Top:    Info banner with startup instructions
 * - Center: Three order columns (Ship, Pickup, Delivery) on the left;
 *             action buttons, order detail panel on the right
 * - Bottom: Output log for action feedback messages
 */
public class MainGUI extends Application {

    private OrderManager orderManager;
    private Directory directory;

    // List of orders on the left, divided into columns
    private ObservableList<Order> shipItems     = FXCollections.observableArrayList();
    private ObservableList<Order> pickupItems   = FXCollections.observableArrayList();
    private ObservableList<Order> deliveryItems = FXCollections.observableArrayList();

    private ListView<Order> shipListView     = new ListView<>(shipItems);
    private ListView<Order> pickupListView   = new ListView<>(pickupItems);
    private ListView<Order> deliveryListView = new ListView<>(deliveryItems);

    // Right-side detail panel
    private VBox detailPanel = new VBox(6);

    // Tracks whether the uncompleted filter is currently active
    private boolean showingUncompleted = false;

    // The toggle button (kept as a field so we can update its text)
    private Button btnUncompleted = new Button("Show Uncompleted Orders");

    // Bottom output log
    private TextFlow outputArea = new TextFlow();

    //undo feature
    private UndoManager undoManager = new UndoManager();

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
     *
     * @param primaryStage the JavaFX primary stage
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

        // Each row shows an icon and status
        for (ListView<Order> lv : List.of(shipListView, pickupListView, deliveryListView)) {
            lv.setCellFactory(listView -> new ListCell<>() {
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
                    } else if (order.getOrderStatus() == OrderStatus.CANCELED) {
                        icon = "❌";
                    } else if (order.getOrderStatus() == OrderStatus.IN_PROGRESS) {
                        icon = "⏳";
                    } else {
                        icon = "📦";
                    }

                    setText(icon + "Order #" + order.getOrderID()
                            + " | " + order.getOrderStatus());

                    String color;
                    if (order.getOrderStatus() == OrderStatus.CANCELED) {
                        color = "gray";
                    } else if (order.getOrderStatus() == OrderStatus.COMPLETED) {
                        color = "green";
                    } else if (order.getOrderStatus() == OrderStatus.IN_PROGRESS) {
                        color = "blue";
                    } else {
                        color = "black";
                    }

                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            });
        }

        // When an order is selected in one column, clear selection in the other two
        shipListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                pickupListView.getSelectionModel().clearSelection();
                deliveryListView.getSelectionModel().clearSelection();
            }
        });

        pickupListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                shipListView.getSelectionModel().clearSelection();
                deliveryListView.getSelectionModel().clearSelection();
            }
        });

        deliveryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                shipListView.getSelectionModel().clearSelection();
                pickupListView.getSelectionModel().clearSelection();
            }
        });


        // Each column has a colored header and shows only its order type
        VBox shipCol     = makeOrderColumn("🛫  Ship",     shipListView,     "#dbeeff", "#1a5fa8");
        VBox pickupCol   = makeOrderColumn("🛒  Pickup",   pickupListView,   "#fff3e0", "#a05a00");
        VBox deliveryCol = makeOrderColumn("🚗  Delivery", deliveryListView, "#fde8e8", "#9b2020");

        HBox orderColumns = new HBox(8, shipCol, pickupCol, deliveryCol);
        HBox.setHgrow(shipCol,     Priority.ALWAYS);
        HBox.setHgrow(pickupCol,   Priority.ALWAYS);
        HBox.setHgrow(deliveryCol, Priority.ALWAYS);

        // right side - buttons
        Button btnStart      = new Button("Start Order");
        Button btnComplete   = new Button("Complete Order");
        Button btnDisplay    = new Button("Display Order");
        Button btnCancel     = new Button("Cancel Order");
        Button btnExport     = new Button("Export All Orders");
        Button btnUndo       = new Button("Undo Last Action");

        // make all buttons same width
        for (Button b : new Button[]{btnStart, btnComplete,
                btnDisplay, btnCancel, btnUndo, btnUncompleted, btnExport}) {
            b.setMaxWidth(Double.MAX_VALUE);
        }

        VBox buttonPanel = new VBox(8,
                new Label("Actions:"),
                btnStart,
                btnComplete,
                btnDisplay,
                btnCancel,
                btnUndo,
                new Separator(),
                btnUncompleted,
                btnExport,
                new Separator()
        );
        // Detail panel setup - shows info for the selected order
        Label detailHeader = new Label("Order Details");
        detailHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        detailPanel.getChildren().add(new Label("Select an order and click Display."));
        detailPanel.setStyle(
                "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 10;"
        );
        VBox.setVgrow(detailPanel, Priority.ALWAYS);


        // Right panel = buttons on top, detail panel below
        VBox rightPanel = new VBox(10, buttonPanel, detailHeader, detailPanel);
        rightPanel.setPrefWidth(260);
        rightPanel.setMinWidth(260);
        rightPanel.setPadding(new Insets(0, 0, 0, 10));

        // bottom - output messages
        ScrollPane scrollPane = new ScrollPane(outputArea);
        scrollPane.setPrefHeight(150);
        scrollPane.setFitToWidth(true);
        // auto scroll to bottom when new text is added
        outputArea.heightProperty().addListener((obs, oldVal, newVal) ->
                scrollPane.setVvalue(1.0));

        // connect buttons to their actions
        btnStart.setOnAction(e -> handleStart());
        btnComplete.setOnAction(e -> handleComplete());
        btnDisplay.setOnAction(e -> handleDisplay());
        btnCancel.setOnAction(e -> handleCancel());
        btnUndo.setOnAction(e -> handleUndo());
        btnUncompleted.setOnAction(e -> handleToggleUncompleted());
        btnExport.setOnAction(e -> handleExport());

        // Info banner on top - tells users how to get started
        Label infoBanner = new Label(
                "System ready!  |  Drop order files into the \"watched\" folder  |  " +
                        "Auto-imports every 3 seconds  |  To reset: delete \"allOrders.dat\" & \"importedFiles.dat\""
        );
        infoBanner.setMaxWidth(Double.MAX_VALUE);
        infoBanner.setWrapText(true);
        infoBanner.setStyle(
                "-fx-background-color: #dbeeff;" +
                        "-fx-text-fill: #1a5fa8;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 12 8 12;" +
                        "-fx-background-radius: 6;"
        );

        // put it all together
        HBox centerPanel = new HBox(orderColumns, rightPanel);
        HBox.setHgrow(orderColumns, Priority.ALWAYS);
        VBox root = new VBox(10, infoBanner, centerPanel, new Label("Output:"), scrollPane);
        root.setPadding(new Insets(15));

        // show the window
        primaryStage.setTitle("Warehouse Order System");
        primaryStage.setScene(new Scene(root, 1100, 800));
        primaryStage.show();
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(800);

        refreshOrderList();
    }

    /**
     * Helper - checks all three list views for a selected order.
     * Logs a prompt message and returns null if nothing is selected.
     *
     * @return the selected Order, or null if none is selected
     */
    private Order getSelectedOrder() {
        for (ListView<Order> lv : List.of(shipListView, pickupListView, deliveryListView)) {
            Order selected = lv.getSelectionModel().getSelectedItem();
            if (selected != null) return selected;
        }
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Order Selected");
        alert.setHeaderText("Please select an order first.");
        alert.setContentText("Click on an order from the Ship, Pickup, or Delivery column before performing an action.");
        alert.showAndWait();
        return null;
    }

    /**
     * Builds a labeled column with a colored header for one order type.
     *
     * @param title      the column header text (e.g. "🛫  Ship")
     * @param listView   the ListView to embed in the column
     * @param headerBg   background color for the header
     * @param headerText text color for the header
     * @return a VBox containing the styled header and list
     */

    private VBox makeOrderColumn(String title, ListView<Order> listView,
                                 String headerBg, String headerText) {
        Label header = new Label(title);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setPadding(new Insets(6, 10, 6, 10));
        header.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-color: " + headerBg + ";" +
                        "-fx-text-fill: " + headerText + ";" +
                        "-fx-background-radius: 6 6 0 0;"
        );
        listView.setPrefHeight(280);
        VBox col = new VBox(0, header, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
        col.setStyle(
                "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;"
        );
        return col;
    }

    /**
     * Start button - moves the selected order from INCOMING to IN_PROGRESS.
     */
    private void handleStart() {
        Order selected = getSelectedOrder();
        if (selected == null) return;

        int orderID = selected.getOrderID();
        Order order = orderManager.getAllOrders().get(orderID);
        if(order == null) return;
        undoManager.saveState(order);
        OrderStatus statusBefore = order.getOrderStatus();
        orderManager.startOrder(orderID);


        if(statusBefore == OrderStatus.CANCELED) {
            log("Order #" + orderID + " cannot be started. It has been canceled.");
        } else if (statusBefore == OrderStatus.IN_PROGRESS) {
            log("Order #" + orderID + " is already started.", Color.RED);
        } else if (statusBefore == OrderStatus.COMPLETED) {
            log("Order #" + orderID + " is already completed.", Color.RED);
        } else {
            String startTime = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                    .format(new java.util.Date(orderManager.getAllOrders().get(orderID).getStartedAt()));
            log("Started order #" + orderID + " at " + startTime, Color.BLUE);
        }

        refreshOrderList();
    }

    /**
     * Complete button - moves the selected order from IN_PROGRESS to COMPLETED.
     */
    private void handleComplete() {
        Order selected = getSelectedOrder();
        if (selected == null) return;

        int orderID = selected.getOrderID();
        Order order = orderManager.getAllOrders().get(orderID);
        if(order == null) return;

        undoManager.saveState(order);
        OrderStatus statusBefore = orderManager.getAllOrders().get(orderID).getOrderStatus();
        orderManager.completeOrder(orderID);

        if(statusBefore == OrderStatus.CANCELED) {
            log("Order #" + orderID + " has been canceled. Canceled orders cannot be completed.");
        } else if (statusBefore == OrderStatus.COMPLETED) {
            log("Order #" + orderID + " is already completed.", Color.RED);
        } else if (statusBefore == OrderStatus.INCOMING) {
            log("Order #" + orderID + " cannot be completed. Must be started first.", Color.RED);
        } else {
            String completeTime = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                    .format(new java.util.Date(orderManager.getAllOrders().get(orderID).getCompletedAt()));
            log("Completed order #" + orderID + " at " + completeTime, Color.GREEN);
        }

        refreshOrderList();
    }

    /**
     * Display button - shows full details of the selected order in the output log.
     */
    private void handleDisplay() {
        Order selected = getSelectedOrder();
        if (selected == null) return;

        int orderID = selected.getOrderID();
        Order order = orderManager.getAllOrders().get(orderID);
        if(order == null){
            log("Order not found.");
            return;
        } else if (order.getOrderStatus() == OrderStatus.CANCELED) {
            log("Canceled orders cannot be displayed.");
            return;
        }

        detailPanel.getChildren().clear();

        String typeIcon = order.getType().equalsIgnoreCase("delivery") ? "🚗"
                : order.getType().equalsIgnoreCase("ship") ? "🛫" : "🛒";
        String typeColor = order.getType().equalsIgnoreCase("delivery") ? "#9b2020"
                : order.getType().equalsIgnoreCase("ship") ? "#1a5fa8" : "#a05a00";
        String typeBg = order.getType().equalsIgnoreCase("delivery") ? "#fde8e8"
                : order.getType().equalsIgnoreCase("ship") ? "#dbeeff" : "#fff3e0";

        Label header = new Label(typeIcon + " Order #" + order.getOrderID() + " — " + order.getType());
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;" +
                "-fx-text-fill: " + typeColor + ";" +
                "-fx-background-color: " + typeBg + ";" +
                "-fx-padding: 6 10 6 10; -fx-background-radius: 4;");
        header.setMaxWidth(Double.MAX_VALUE);

        detailPanel.getChildren().add(header);
        detailPanel.getChildren().add(makeDetailRow("Status", order.getOrderStatus().toString()));

        if (order.getStartedAt() != 0) {
            String started = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                    .format(new java.util.Date(order.getStartedAt()));
            detailPanel.getChildren().add(makeDetailRow("Started", started));
        }
        if (order.getCompletedAt() != 0) {
            String completed = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                    .format(new java.util.Date(order.getCompletedAt()));
            detailPanel.getChildren().add(makeDetailRow("Completed", completed));
        }

        if (order.getOrderDate() != 0)
            detailPanel.getChildren().add(makeDetailRow("Date", String.valueOf(order.getOrderDate())));
        if (order.getSourceFile() != null)
            detailPanel.getChildren().add(makeDetailRow("Source", order.getSourceFile()));

        Label itemsLabel = new Label("Items:");
        itemsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        detailPanel.getChildren().add(itemsLabel);

        for (Item item : order.getItems()) {
            Label itemLine = new Label("  • " + item.getName()
                    + " (x" + item.getQuantity() + ") — $" + item.getPrice());
            itemLine.setStyle("-fx-font-size: 12px;");
            detailPanel.getChildren().add(itemLine);
        }
    }

    /**
     * Builds a label-value row for the detail panel.
     *
     * @param label the field name (e.g. "Status")
     * @param value the field value (e.g. "IN_PROGRESS")
     * @return an HBox containing the styled label and value
     */
    private HBox makeDetailRow(String label, String value) {
        Label l = new Label(label + ":");
        l.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
        l.setPrefWidth(80);
        Label v = new Label(value);
        v.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        v.setWrapText(true);
        v.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(v, Priority.ALWAYS);
        return new HBox(8, l, v);
    }

    /**
     * Cancel button - cancels the selected order after prompting the user for a reason.
     * Feature 1
     */
    private void handleCancel() {
        Order selected = getSelectedOrder();
        if (selected == null) return;

        int orderID = selected.getOrderID();
        Order order = orderManager.getAllOrders().get(orderID);
        if (order == null) return;

        OrderStatus statusBefore = order.getOrderStatus();
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

                undoManager.saveState(orderManager.getAllOrders().get(orderID));
                orderManager.cancelOrder(orderID);
                if (statusBefore == OrderStatus.IN_PROGRESS) {
                    String cancelTime = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                            .format(new java.util.Date(order.getCanceledAt()));
                    log("Order #" + orderID + " has stopped being fulfilled and been canceled at " + cancelTime + ". Reason: " + reason.get());
                } else {
                    String cancelTime = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                            .format(new java.util.Date(order.getCanceledAt()));
                    log("Order #" + orderID + " is canceled at " + cancelTime + ". Reason: " + reason.get());
                }
            } else {
                log("No reason entered. Order has not been canceled");
            }
        }

        refreshOrderList();
    }


    /**
     * Handles "Show Uncompleted Orders" / "Show All Orders" toggle.
     * When active, hides completed orders from all three columns.
     * When inactive, shows all orders again.
     * Button text updates to reflect the current state.
     */
    private void handleToggleUncompleted() {
        showingUncompleted = !showingUncompleted;

        if (showingUncompleted) {
            btnUncompleted.setText("Show All Orders");
            btnUncompleted.setStyle("-fx-font-weight: bold; -fx-text-fill: #9b2020;");
            log("Showing uncompleted orders only.", Color.RED);
        } else {
            btnUncompleted.setText("Show Uncompleted Orders");
            btnUncompleted.setStyle("");
            log("Showing all orders.");
        }

        refreshOrderList();
    }

    /**
     * Exports all orders to a JSON and XML file
     */
    private void handleExport() {
        orderManager.exportXML();
        orderManager.exportJSON();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Successful");
        alert.setHeaderText("Orders exported successfully.");
        alert.setContentText("All orders have been saved to the \"exported xml files\" and \"exported json files\" folder.");
        alert.showAndWait();
        log("Orders exported successfully. All exported orders are in the \"exported xml files\" and \"exported json files\" folder.");
    }

    /**
     * Undo last action for order
     */
    private void handleUndo(){
        boolean success = undoManager.undo(orderManager);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Undo");

        if(success){
            alert.setHeaderText("Undo successful.");
            alert.setContentText("The last action has been successfully undone.");
            log("Undo Successful.", Color.GREEN);
        }else{
            alert.setHeaderText("Nothing to undo.");
            alert.setContentText("There are no actions to undo.");
            log("Nothing to undo.", Color.RED);
        }

        alert.showAndWait();
        refreshOrderList();
    }

    /**
     * Updates the order list on the left side of the window
     */
    private void refreshOrderList() {
        shipItems.clear();
        pickupItems.clear();
        deliveryItems.clear();
        for (Map.Entry<Integer, Order> entry : orderManager.getAllOrders().entrySet()) {
            Order order = entry.getValue();
            if (showingUncompleted && order.getOrderStatus() == OrderStatus.COMPLETED) continue;
            switch (order.getType().toLowerCase()) {
                case "ship"     -> shipItems.add(order);
                case "pickup"   -> pickupItems.add(order);
                case "delivery" -> deliveryItems.add(order);
            }
        }
    }

    /**
     * Prints a colored message to the output area at the bottom
     *
     * @param message the text to display
     * @param color   the color of the text
     */
    private void log(String message, Color color) {
        Text text = new Text(message + "\n");
        text.setFill(color);
        outputArea.getChildren().add(text);
    }

    /**
     * Prints a black message to the output area at the bottom.
     *
     * @param message the text to display
     */
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