package edu.metrostate;

public abstract class Order {
    protected int orderID;
    protected long orderDate;

    public Order(int orderID, long orderDate){
        this.orderID = orderID;
        this.orderDate = orderDate;;
    }
}
