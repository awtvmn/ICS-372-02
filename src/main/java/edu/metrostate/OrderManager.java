package edu.metrostate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * OrderManager class, used to add, start, display, cancel, complete or incomplete orders,
 * and export orders into a JSON file
 */
public class OrderManager implements Serializable {

    private HashMap<Integer, Order> allOrders = new HashMap<>();
    private static int nextOrderID = 1;
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Adds a new order of the given type to the system.
     * Requirement 2 and 3.
     *
     * @param type      "ship", "pickup", or "delivery"
     * @param orderDate timestamp of the order
     * @param items     list of items in the order
     */
    public void addOrder(String type, long orderDate, ArrayList<Item> items) {
        Order newOrder;

        // checks if it is ship or pickup
        if (type.equals("ship")) {
            newOrder = new ShipOrder(nextOrderID, orderDate, items);
        } else if (type.equals("pickup")) {
            newOrder = new PickupOrder(nextOrderID, orderDate, items);
        } else if (type.equals("delivery")) {
            newOrder = new DeliveryOrder(nextOrderID, orderDate, items);
        } else {
            System.out.println("Unknown order type.");
            return;
        }


        // Assigns new order to an ID
        allOrders.put(nextOrderID, newOrder); //puts it in the hash
        System.out.println("Order " + nextOrderID + " added successfully.");
        nextOrderID++; // increm by 1

    }

    /**
     * Adds a new order using the order ID from imported XML file.
     * If the ID already exists, reassigns a new ID so no order is lost.
     * Feature 3.
     *
     * @param orderID    the order ID from the XML file
     * @param type       "ship", "pickup", or "delivery"
     * @param items      list of items in the order
     * @param sourceFile the name of the file this order came from
     */
    public void addOrderWithID(int orderID, String type, ArrayList<Item> items, String sourceFile) {
        if (allOrders.containsKey(orderID)) {
            System.out.println("Order #" + orderID + " already exists, Reassigning to #" + nextOrderID);
            orderID = nextOrderID;
        }

        Order newOrder;
        if (type.equals("ship")) {
            newOrder = new ShipOrder(orderID,0, items);
        } else if (type.equals("pickup")) {
            newOrder = new PickupOrder(orderID, 0, items);
        } else if (type.equals("delivery")) {
            newOrder = new DeliveryOrder(orderID, 0, items);
        } else {
            System.out.println("Unknown order type: " + type);
            return;
        }

        newOrder.setSourceFile(sourceFile);

        allOrders.put(orderID, newOrder);

        if (orderID >= nextOrderID) nextOrderID = orderID + 1;
        System.out.println("Order #" + orderID + " imported successfully.");
    }




    /**
     * Starts fulfilling order, moving it from INCOMING to IN_PROGRESS.
     * Requirement 4
     *
     * @param orderID the ID of the order to start
     */
    public void startOrder(int orderID) {
        Order order = allOrders.get(orderID); // gets info from hash
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        if (order.startFulfilling()){
            System.out.println("Order " + orderID + " has started.");
        } else {
            System.out.println("Order " + orderID + " cannot start. Status: " + order.getOrderStatus());
        }

    }

    /**
     * Displays the details of an order
     * Requirement 4
     *
     * @param orderID the ID of the order to display
     */
    public void displayOrder(int orderID){
        Order order = allOrders.get(orderID); // gets info from hash
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        order.displayOrder();
    }

    /**
     * Completes an order, moving it from IN_PROGRESS to COMPLETED.
     * Requirement 4
     *
     * @param orderID the ID of the order to complete
     */
    public void completeOrder(int orderID){
        Order order = allOrders.get(orderID);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        if (order.completeOrder()){
            System.out.println("Order " + orderID + " has been completed.");
        } else {
            System.out.println("Order " + orderID + " cannot be completed. Status: " + order.getOrderStatus());
        }

    }

    /**
     * Prints all orders that have not yet been completed.
     * Requirement 8
     */
    public void incompleteOrder(){
        boolean hasUncompletedOrders = false;
        System.out.println("Uncompleted Orders: ");
        System.out.println();

        for(Order order : allOrders.values()){
            if(order.getOrderStatus() != OrderStatus.COMPLETED){
                displayOrder(order.getOrderID());
                System.out.println();
                hasUncompletedOrders = true;
            }
        }

        if(!hasUncompletedOrders){
            System.out.println("No uncompleted orders.");
        }
    }

    /**
     * Cancels an order if it is INCOMING or IN_PROGRESS.
     * Feature 1
     *
     * @param orderID the ID of the order to cancel
     */
    public void cancelOrder(int orderID){
        Order order = allOrders.get(orderID);
        if(order == null) {
            System.out.println("Order not found.");
            return;
        }
        if (order.cancelOrder()){
            System.out.println("Order " + orderID + " has been canceled.");

        } else {
            System.out.println("Order " + orderID + " has been canceled or completed already.");
        }
    }

    /**
     * Returns all orders in the system.
     * Used by the GUI to populate the order list.
     *
     * @return map of order ID to Order
     */
    public HashMap<Integer, Order> getAllOrders() {
        return allOrders;
    }

    /**
     * Exports all orders to a JSON file called "exported json files"
     * Requirement 7
     */
    public void exportOrders() {
        JSONObject root = new JSONObject();
        JSONArray ordersArray = new JSONArray();
        for (Order order : allOrders.values()){
            JSONObject orderJson = new JSONObject();
            orderJson.put("order_id",order.getOrderID());
            orderJson.put("type",order.getType());
            orderJson.put("order_date",order.getOrderDate());
            orderJson.put("status",order.getOrderStatus());
            if (!order.getOrderStatus().toString().equals("COMPLETED")){
                orderJson.put("price total ",order.getTotalPrice());
            }

            JSONArray itemsArray = new JSONArray();
            for (Item item : order.getItems()){
                JSONObject itemJson = new JSONObject();
                itemJson.put("name",item.getName());
                itemJson.put("Quantity",item.getQuantity());
                itemJson.put("price",item.getPrice());
                itemsArray.add(itemJson);
            }
            orderJson.put("items", itemsArray);
            ordersArray.add(orderJson);
        }
        root.put("orders", ordersArray);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(root);

        try (FileWriter writer = new FileWriter("exported json files/all-orders.json")) {
            writer.write(prettyJson);
            System.out.println("All orders exported successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads previously saved orders from disk when the program starts.
     * Feature 2
     */
    public void loadOrders() {
        File file = new File("allOrders.dat");
        if(!file.exists()) {
            return;
        }
        try {
            ObjectInputStream temp = new ObjectInputStream( new FileInputStream(file));
            allOrders = (HashMap<Integer, Order>) temp.readObject();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Saves all current orders to disk when the program stops.
     * Feature 2
     */
    public void saveOrder() {
        try {
            ObjectOutputStream temp = new ObjectOutputStream(new FileOutputStream("allOrders.dat"));
            temp.writeObject(allOrders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
