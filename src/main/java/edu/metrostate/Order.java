package edu.metrostate;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Order class, used to create different orders
 * Abstract class for ShipOrder, PickupOrder, and DeliveryOrder.
 */
public abstract class Order implements Serializable {
    protected int orderID;
    protected long orderDate;
    protected OrderStatus status;
    protected ArrayList<Item> items;
    protected String sourceFile;
    protected String type;
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new Order.
     *
     * @param orderID   unique identifier for this order
     * @param orderDate timestamp when the order was placed, or 0 if not available
     * @param items     list of items in the order
     * @param type      the type of order: "ship", "pickup", or "delivery"
     */
    public Order(int orderID, long orderDate, ArrayList<Item> items, String type) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = OrderStatus.INCOMING;
        this.items = items;
        this.type = type;
    }

    /**
     * Sets the source file this order was imported from.
     * Feature 3.
     *
     * @param sourceFile the name of the XML file this order came from
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * Returns the source file this order was imported from.
     * Returns null if the order was not imported from a file.
     * Feature 3.
     *
     * @return the source file name, or null
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /** Returns the order ID.
     * @return order ID */
    public int getOrderID(){ return orderID; }

    /** Returns the order date, or 0 if not available.
     * @return order date */
    public long getOrderDate(){
        return orderDate;
    }

    /** Returns the current status of the order.
     * @return order status */
    public OrderStatus getOrderStatus() {
        return status;
    }

    /** Returns the order type: "ship", "pickup", or "delivery".
     * @return order type */
    public String getType() {return type; }

    /** Returns the list of items in the order.
     * @return list of items */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * startFulfilling method, returns true if incoming orders need to be started
     * @return boolean */
    public boolean startFulfilling() {
        if (status == OrderStatus.INCOMING){
            status = OrderStatus.IN_PROGRESS;
            return true;
        }
        return false;
    }

    /**
     * cancelOrder method, returns true if order has been canceled
     * feature 1
     * @return boolean
     */
    public boolean cancelOrder() {
        if (status == OrderStatus.INCOMING){
            status = OrderStatus.CANCELED;
            return true;
        } else if (status == OrderStatus.IN_PROGRESS){
            status = OrderStatus.CANCELED;
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

    /** Returns a string describing the order type.
     * @return type string */
    public String getOrderType(){
        if (type.equalsIgnoreCase("delivery")){
            return "DELIVERY order";
        }else if (type.equalsIgnoreCase("ship")){
            return "SHIP order";
        }else if (type.equalsIgnoreCase("pickup")){
            return "PICKUP order";
        } else{
            return type.toUpperCase();
        }
    }

    /**
     * displayOrder method, prints out an order's id, type, date, status, and items
     */
    public void displayOrder() {
        System.out.println("Order ID: " + orderID);
        if (orderDate != 0) {
            System.out.println("Date: " + orderDate);
        }
        System.out.println(getOrderType() + " is " + getOrderStatus());
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
