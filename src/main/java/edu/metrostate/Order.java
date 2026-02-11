package edu.metrostate;

import java.util.ArrayList;

/**
 * Order class, used to create different orders
 */
public abstract class Order {
    protected int orderID;
    protected long orderDate;
    protected OrderStatus status;
    protected ArrayList<Item> items;
    protected String type;

    //constructor
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

    /**
     * startFulfilling method, returns true if incoming orders need to be started
     * @return boolean
     */
    public boolean startFulfilling() {
        if (status == OrderStatus.INCOMING){
            status = OrderStatus.IN_PROGRESS;
            return true;
        }
        return false;
    }

    /**
     * completeOrder method, returns true if in progress orders can be completed
     * @return boolean
     */
    public boolean completeOrder() {
        if (status == OrderStatus.IN_PROGRESS) {
            status = OrderStatus.COMPLETED;
            return true;
        }
        return false;
    }

    /**
     * getTotalPrice method, gets the total price of all items
     * @return double
     */
    public double getTotalPrice() {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    /**
     * displayOrder method, prints out an order's id, type, date, status, and items
     */
    public void displayOrder() {
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

    /**
     * add method, adds item objects into an array for an order
     * @param item item
     */
    public void add(Item item){
        items.add(item);
     }
}
