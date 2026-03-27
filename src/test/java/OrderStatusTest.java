import edu.metrostate.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrderStatus transitions.
 * Tests state changes for INCOMING, IN_PROGRESS, COMPLETED, and CANCELED.
 */
public class OrderStatusTest {

    @Test
    void newOrderIsIncomingTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Test Item", 10.00, 1));
        Order order = new ShipOrder(1, 0, items);
        assertEquals(OrderStatus.INCOMING, order.getOrderStatus());
    }

    @Test
    void startOrderIncomingToInProgressTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Test Item", 10.00, 1));
        Order order = new ShipOrder(2, 0, items);

        assertTrue(order.startFulfilling());
        assertEquals(OrderStatus.IN_PROGRESS, order.getOrderStatus());
    }

    @Test
    void startOrderAlreadyInProgressFailsTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Test Item", 10.00, 1));
        Order order = new ShipOrder(3, 0, items);
        order.startFulfilling();

        assertFalse(order.startFulfilling());
        assertEquals(OrderStatus.IN_PROGRESS, order.getOrderStatus());
    }

    @Test
    void completeOrderInProgressToCompletedTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Test Item", 10.00, 1));
        Order order = new PickupOrder(4, 0, items);
        order.startFulfilling();

        assertTrue(order.completeOrder());
        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
    }

    @Test
    void completeOrderIncomingFailsTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Test Item", 10.00, 1));
        Order order = new DeliveryOrder(5, 0, items);

        assertFalse(order.completeOrder());
        assertEquals(OrderStatus.INCOMING, order.getOrderStatus());
    }

    @Test
    void cancelOrderIncomingTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Test Item", 10.00, 1));
        Order order = new ShipOrder(6, 0, items);

        assertTrue(order.cancelOrder());
        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
    }

    @Test
    void cancelOrderInProgressTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Test Item", 10.00, 1));
        Order order = new ShipOrder(7, 0, items);
        order.startFulfilling();

        assertTrue(order.cancelOrder());
        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
    }

    @Test
    void cancelOrderCompletedFailsTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Test Item", 10.00, 1));
        Order order = new PickupOrder(8, 0, items);
        order.startFulfilling();
        order.completeOrder();

        assertFalse(order.cancelOrder());
        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
    }
}