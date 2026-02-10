package edu.metrostate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

public class OrderManager {
    private HashMap<Integer, Order> allOrders = new HashMap<>();
    private static int nextOrderID = 1;

    //Requirement 2 & 3: add new orders
    public int addOrder(String type, long orderDate, ArrayList<Item> items) {
        Order newOrder;

        // checks if it is ship or pickup
        if (type.equals("ship")) {
            newOrder = new ShipOrder(nextOrderID, orderDate, items);
        } else if (type.equals("pickup")) {
            newOrder = new PickupOrder(nextOrderID, orderDate, items);
        } else {
            System.out.println("Unknown order type.");
            return -1;
        }

        // Assigns new order to an ID
        allOrders.put(nextOrderID, newOrder); //puts it in the hash
        System.out.println("Order " + nextOrderID + " added successfully.");
        nextOrderID++; // increm by 1

        return 0;
    }

    // Requirement 4: start order
    public void startOrder(int orderID) {
        Order order = allOrders.get(orderID); // gets info from hash
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        if (order.startFulfilling()){
            System.out.println("Order " + orderID + " has started.");
        } else {
            System.out.println("Order " + orderID + " cannot start because " + order.getOrderStatus());
        }

    }

    // Requirement 4: display order
    public void displayOrder(int orderID){
        Order order = allOrders.get(orderID); // gets info from hash
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        order.displayorder();
    }

    // Requirement 4: completed order
    public void completeOrder(int orderID){
        Order order = allOrders.get(orderID);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        if (order.completeOrder()){
            System.out.println("Order " + orderID + " has been completed.");
        } else {
            System.out.println("Order " + orderID + " cannot start because " + order.getOrderStatus());
        }

    }

    // show uncompleted orders
    // export all orders




}