package edu.metrostate;

import java.util.ArrayList;

/**
 * PickupOrder class, extends order, classifies an order as a pickup order
 */
public class PickupOrder extends Order{

    /**
     * Constructs a new PickupOrder.
     *
     * @param orderID   unique identifier for this order
     * @param orderDate timestamp when the order was placed
     * @param items     list of items in the order
     */
    public PickupOrder(int orderID, long orderDate, ArrayList<Item> items) {
        super(orderID, orderDate, items, "pickup");
    }
}
