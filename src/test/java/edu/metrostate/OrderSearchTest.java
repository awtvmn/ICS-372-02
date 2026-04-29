package edu.metrostate;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UnitTest created for the OrderSearch class
 * Creates a fake order class in order to test
 * Then tests both methods in OrderSearch
 */
class OrderSearchTest {

    //Order class is abstract, created a similar order class for testing
    private static class FakeOrder extends edu.metrostate.Order {
        public FakeOrder(int id, long date, ArrayList<Item> items, String type) {
            super(id, date, items, type);
        }
    }


    @Test
    void countMatchTest() {
        //first order
        ArrayList<Item> order1 = new ArrayList<>();
        order1.add(new Item("Cheetos", 2.90, 4));
        order1.add(new Item("Cellphone", 190.00, 1));
        order1.add(new Item("Tv", 360.90, 1));

        edu.metrostate.Order o1 = new FakeOrder(1, 290, order1, "Pickup");

        //second order
        ArrayList<Item> order2 = new ArrayList<>();
        order2.add(new Item("Microphone", 59.86, 2));
        order2.add(new Item("4k Camera", 206.99, 1));
        order2.add(new Item("Cellphone", 190.00, 1));

        edu.metrostate.Order o2 = new FakeOrder(2, 306, order2, "Delivery");

        //third order
        ArrayList<Item> order3 = new ArrayList<>();
        order3.add(new Item("Potato Chips", 2.00, 2));
        order3.add(new Item("12 Pack Coke", 8.09, 1));

        edu.metrostate.Order o3 = new FakeOrder(3, 102, order3, "Pickup");

        List<edu.metrostate.Order> orders = List.of(o1, o2, o3);

        OrderSearch orderSearch = new OrderSearch();
        int count1 = orderSearch.countMatch(orders, "Cellphone");
        int count2 = orderSearch.countMatch(orders, "Cellphone");

        assertEquals(2, count1);
        assertEquals(2, count2);
    }
    @Test
    void searchForOrderTest() {
        //first order
        ArrayList<Item> order1 = new ArrayList<>();
        order1.add(new Item("Cheetos", 2.90, 4));
        order1.add(new Item("Cellphone", 190.00, 1));
        order1.add(new Item("Tv", 360.90, 1));

        edu.metrostate.Order o1 = new FakeOrder(1, 290, order1, "Pickup");

        //second order
        ArrayList<Item> order2 = new ArrayList<>();
        order2.add(new Item("Microphone", 59.86, 2));
        order2.add(new Item("4k Camera", 206.99, 1));
        order2.add(new Item("Cellphone", 190.00, 1));

        edu.metrostate.Order o2 = new FakeOrder(2, 306, order2, "Delivery");

        //third order
        ArrayList<Item> order3 = new ArrayList<>();
        order3.add(new Item("Potato Chips", 2.00, 2));
        order3.add(new Item("12 Pack Coke", 8.09, 1));

        edu.metrostate.Order o3 = new FakeOrder(3, 102, order3, "Pickup");

        List<edu.metrostate.Order> orders = List.of(o1, o2, o3);

        OrderSearch orderSearch = new OrderSearch();
        List<Order> res = orderSearch.findMatch(orders, "Cellphone");

        assertEquals(2, res.size());
        assertTrue(res.contains(o1));
        assertFalse(res.contains(o3));
        assertTrue(res.stream().anyMatch(o -> o.getOrderID() == 1));
    }
}