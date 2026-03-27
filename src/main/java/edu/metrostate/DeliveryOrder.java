package edu.metrostate;

import java.util.ArrayList;

/**
 * DeliveryOrder class, extends order, classifies an order as a delivery order
 */
public class DeliveryOrder extends Order {
    public DeliveryOrder(int orderID, long orderDate, ArrayList<Item> items){
        super(orderID, orderDate, items, "delivery");
    }
}
