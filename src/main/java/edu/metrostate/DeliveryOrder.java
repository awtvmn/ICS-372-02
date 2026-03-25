package edu.metrostate;

import java.util.ArrayList;

public class DeliveryOrder extends Order {
    public DeliveryOrder(int orderID, long orderDate, ArrayList<Item> items){
        super(orderID, orderDate, items, "delivery");
    }
}
