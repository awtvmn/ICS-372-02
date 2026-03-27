import edu.metrostate.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;

/**
 * Unit tests for the PickShipDelivery class.
 * Verifies that each order stores ID, date, type and items correctly
 */
public class PickShipDeliveryTest {

    //test ship order class using mock objects
    @Test
    public void ShipOrderTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("IPad", 250, 1));
        ShipOrder order = new ShipOrder(1, 123, items);

        assertEquals(1, order.getOrderID());
        assertEquals(123, order.getOrderDate());
        assertEquals("ship", order.getType());
        assertEquals(items, order.getItems());

    }

    //test pickup order class
    @Test
    public void PickupOrderTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Notebook", 8, 3));
        PickupOrder order = new PickupOrder(2, 456, items);

        assertEquals(2, order.getOrderID());
        assertEquals(456, order.getOrderDate());
        assertEquals("pickup", order.getType());
        assertEquals(items, order.getItems());

    }

    //test delivery order class
    @Test
    public void DeliveryOrderTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Lipstick", 10, 2));
        DeliveryOrder order = new DeliveryOrder(3, 789, items);

        assertEquals(3, order.getOrderID());
        assertEquals(789, order.getOrderDate());
        assertEquals("delivery", order.getType());
        assertEquals(items, order.getItems());

    }
}
