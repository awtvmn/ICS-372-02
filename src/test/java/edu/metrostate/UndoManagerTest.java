package edu.metrostate;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;


public class UndoManagerTest {

    @Test
    public void undoManagerTest() {
        OrderManager orderManager = new OrderManager();
        UndoManager undoManager = new UndoManager();

        ArrayList<Item> items = new ArrayList<>();
        Order order = new PickupOrder(1, System.currentTimeMillis(), items);

        orderManager.getAllOrders().put(1, order);
        undoManager.saveState(order);
        orderManager.startOrder(1);
        boolean result = undoManager.undo(orderManager);

        assertTrue(result);
        assertEquals(OrderStatus.INCOMING, order.getOrderStatus());

    }

    @Test
    public void undoManagerTest2() {
        OrderManager orderManager = new OrderManager();
        UndoManager undoManager = new UndoManager();

        ArrayList<Item> items = new ArrayList<>();
        Order order = new PickupOrder(2, System.currentTimeMillis(), items);

        orderManager.getAllOrders().put(2, order);
        undoManager.saveState(order);
        orderManager.cancelOrder(2);
        boolean result = undoManager.undo(orderManager);

        assertTrue(result);
        assertEquals(OrderStatus.INCOMING, order.getOrderStatus());

    }

    @Test
    public void undoManagerTest3() {
        OrderManager orderManager = new OrderManager();
        UndoManager undoManager = new UndoManager();

        boolean result = undoManager.undo(orderManager);
        assertFalse(result);
    }
}
