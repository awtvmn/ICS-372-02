package edu.metrostate;

import java.util.ArrayList;

/**
 * PickupOrder class, extends order, classifies an order as a pickup order
 */
public class PickupOrder extends Order{
    public PickupOrder(int orderID, long orderDate, ArrayList<Item> items) {
        super(orderID, orderDate, items, "pickup");
    }
}
