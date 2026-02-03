package edu.metrostate;

import java.util.ArrayList;

public class PickupOrder extends Order{
    public PickupOrder(int orderID, long orderDate, ArrayList<Item> items) {
        super(orderID, orderDate, items);
    }
}
