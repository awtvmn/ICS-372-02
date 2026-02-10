package edu.metrostate;

import org.json.simple.JSONObject;

import java.io.PrintStream;
import java.util.ArrayList;

public abstract class Order {
    protected int orderID;
    protected long orderDate;
    protected OrderStatus status;
    protected ArrayList<Item> items;
    protected String type;

    public Order(int orderID, long orderDate, ArrayList<Item> items, String type) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = OrderStatus.INCOMING;
        this.items = items;
        this.type = type;
    }

    //getters
    public int getOrderID(){
        return orderID;
    }
    public long getOrderDate(){
        return orderDate;
    }
    public OrderStatus getOrderStatus() {
        return status;
    }
    public String getType() {return type; }

    public ArrayList<Item> getItems() {
        return items;
    }

    public boolean startFulfilling() {
        if (status == OrderStatus.INCOMING){
            status = OrderStatus.IN_PROGRESS;
            return true;
        }
        return false;
    }
     public boolean completeOrder() {
        if (status == OrderStatus.IN_PROGRESS) {
            status = OrderStatus.COMPLETED;
            return true;
        }
        return false;
     }

     public double getTotalPrice() {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
     }

     public void displayorder() {
        System.out.println("Order ID: " + orderID);
        System.out.println("Type: "  + type);
        System.out.println("Date: " + orderDate);
        System.out.println("Status: " + status);
        System.out.println("Items: ");
        for (Item item : items) {
            System.out.printf("%s (Qty: %d, Price: $%.2f)\n", item.getName(), item.getQuantity(), item.getPrice());
        }
        System.out.printf("Total  Price: $%.2f\n", getTotalPrice());
     }

     public void add(Item item){
        items.add(item);
     }
}
