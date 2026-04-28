package edu.metrostate;

import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;


/**
 * OrderManager class, used to add, start, cancel, complete or incomplete orders
 * Returns OrderResult for action methods and export orders into xml file and json file
 */
public class OrderManager implements Serializable {

    private HashMap<Integer, Order> allOrders = new HashMap<>();
    private int nextOrderID = 1;
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Adds a new order of the given type to the system.
     *
     * @param type      "ship", "pickup", or "delivery"
     * @param orderDate timestamp of the order
     * @param items     list of items in the order
     */
    public OrderResult addOrder(String type, long orderDate, ArrayList<Item> items) {
        Order newOrder;

        // checks if it is ship or pickup
        if (type.equals("ship")) {
            newOrder = new ShipOrder(nextOrderID, orderDate, items);
        } else if (type.equals("pickup")) {
            newOrder = new PickupOrder(nextOrderID, orderDate, items);
        } else if (type.equals("delivery")) {
            newOrder = new DeliveryOrder(nextOrderID, orderDate, items);
        } else {
            return new OrderResult(false,"Unknown order type." + type);
        }


        // Assigns new order to an ID
        allOrders.put(nextOrderID, newOrder);
        int assignedID = nextOrderID;
        nextOrderID++;
        return new OrderResult(true, "    Order " + assignedID + " added successfully.");

    }

    /**
     * Adds a new order using the order ID from imported XML and JSON file.
     * If the ID already exists, reassigns a new ID so no order is lost.
     *
     * @param orderID    the order ID from the XML or JSON file
     * @param type       "ship", "pickup", or "delivery"
     * @param items      list of items in the order
     * @param sourceFile the name of the file this order came from
     */
    public OrderResult addOrderWithID(int orderID, String type, ArrayList<Item> items, String sourceFile) {
        if (allOrders.containsKey(orderID)) {
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
            return new OrderResult(false, "    Skipping order #" + orderID
                    + ": unknown order type \"" + type + "\" (must be ship, pickup, or delivery).");
        }

        newOrder.setSourceFile(sourceFile);

        allOrders.put(orderID, newOrder);

        if (orderID >= nextOrderID) nextOrderID = orderID + 1;
        return new OrderResult(true, "    Order #" + orderID + " imported successfully.");
    }




    /**
     * Starts fulfilling order, moving it from INCOMING to IN_PROGRESS.
     *
     * @param orderID the ID of the order to start
     */
    public OrderResult startOrder(int orderID) {
        Order order = allOrders.get(orderID); // gets info from hash
        if (order == null) {
            return new OrderResult(false, "Order not found.");
        }
        if (order.startFulfilling()){
            return new OrderResult(true, "Order " + orderID + " has started.");
        } else {
            return new OrderResult(false, "Order " + orderID + " cannot start. Status: " + order.getOrderStatus());
        }

    }

    /**
     * Completes an order, moving it from IN_PROGRESS to COMPLETED.
     *
     * @param orderID the ID of the order to complete
     */
    public OrderResult completeOrder(int orderID){
        Order order = allOrders.get(orderID);
        if (order == null) {
            return new OrderResult(false, "Order not found.");
        }
        if (order.completeOrder()){
            return new OrderResult(true, "Order " + orderID + " has been completed.");
        } else {
            return new OrderResult(false, "Order " + orderID + " cannot be completed. Status: " + order.getOrderStatus());
        }

    }


    /**
     * Cancels an order if it is INCOMING or IN_PROGRESS.
     *
     * @param orderID the ID of the order to cancel
     */
    public OrderResult cancelOrder(int orderID){
        Order order = allOrders.get(orderID);
        if(order == null) {
            return new OrderResult(false, "Order not found.");
        }
        if (order.cancelOrder()){
            return new OrderResult(true, "Order " + orderID + " has been canceled.");

        } else {
            return new OrderResult(false, "Order " + orderID + " cannot be canceled. Status: " + order.getOrderStatus());
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
     * Loads previously saved orders from disk when the program starts.
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
