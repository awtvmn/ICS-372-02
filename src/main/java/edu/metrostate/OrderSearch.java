package edu.metrostate;

import java.util.List;

/**
 * OrderSearch class, part of search feature, moved to a separate class for easier testing
 */
public class OrderSearch {

    /**
     * countMatch - keeps track of the number of matches (this removes the previous method
     * in the gui that was only used for logging)
     * @param orders List<Order>
     * @param query String
     * @return int
     */
    public int countMatch(List<Order> orders, String query) {
        String lower = query.toLowerCase();
        int count = 0;

        for (Order order : orders) {
            boolean matchesID = String.valueOf(order.getOrderID()).toLowerCase().contains(lower);

            boolean matchesItem = order.getItems().stream().anyMatch(item -> item.getName().toLowerCase().contains(lower));

            if (matchesID || matchesItem) {
                count++;
            }
        }
        return count;
    }

    /**
     * findMatch - uses stream to find if the given query is a match with either an orderID
     * or an item in an order
     * @param orders List<Order>
     * @param query String
     * @return List<Order>
     */
    public List<Order> findMatch(List<Order> orders, String query) {
        String lower = query.toLowerCase();

        //stream filters out orders that match ID or item in order
        return orders.stream()
                .filter(order -> String.valueOf(order.getOrderID()).toLowerCase().contains(lower)
                        || order.getItems().stream().anyMatch(item -> item.getName().toLowerCase().contains(lower))).toList();
    }
}
