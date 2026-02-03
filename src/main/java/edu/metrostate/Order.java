package edu.metrostate;

import org.json.simple.JSONObject;

public abstract class Order {
    protected int orderID;
    protected long orderDate;
    protected OrderStatus status;

    public Order(int orderID, long orderDate){
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = OrderStatus.INCOMING;
    }

    public int getOrderID(){
        return orderID;
    }

    public long getOrderDate(){
        return orderDate;
    }
    
    public OrderStatus getOrderStatus() {
        return status;
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
}
