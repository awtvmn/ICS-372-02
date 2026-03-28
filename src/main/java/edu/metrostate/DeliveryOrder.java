package edu.metrostate;

import java.util.ArrayList;

/**
 * DeliveryOrder class, extends order, classifies an order as a delivery order
 */
public class DeliveryOrder extends Order {

    /**
     * Constructs a new DeliveryOrder.
     *
     * @param orderID   unique identifier for this order
     * @param orderDate timestamp when the order was placed
     * @param items     list of items in the order
     */
    public DeliveryOrder(int orderID, long orderDate, ArrayList<Item> items){
        super(orderID, orderDate, items, "delivery");
    }
}
