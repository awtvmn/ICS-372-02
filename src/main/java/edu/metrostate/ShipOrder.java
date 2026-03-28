package edu.metrostate;

import java.util.ArrayList;

/**
 * ShipOrder class, extends order, classifies an order as a shipping order
 */
public class ShipOrder extends Order{

    /**
     * Constructs a new ShipOrder.
     *
     * @param orderID   unique identifier for this order
     * @param orderDate timestamp when the order was placed
     * @param items     list of items in the order
     */
    public ShipOrder(int orderID, long orderDate,  ArrayList<Item> items) {
        super(orderID, orderDate,  items, "ship");
    }
}
