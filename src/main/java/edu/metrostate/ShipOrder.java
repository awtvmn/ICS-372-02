package edu.metrostate;

import java.util.ArrayList;

/**
 * ShipOrder class, extends order, classifies an order as a shipping order
 */
public class ShipOrder extends Order{
    public ShipOrder(int orderID, long orderDate,  ArrayList<Item> items) {
        super(orderID, orderDate,  items, "ship");
    }
}
