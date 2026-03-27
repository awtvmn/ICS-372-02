import edu.metrostate.DeliveryOrder;
import edu.metrostate.Item;
import edu.metrostate.PickupOrder;
import edu.metrostate.ShipOrder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Item and Order class.
 * Covers creating item, total price calculation and returning item
 */
public class ItemOrderTest {

    @Test
    void createItemTest() {
        Item item1 = new Item("Backpack", 12.98, 1);
        assertEquals("Backpack", item1.getName());
        assertEquals(12.98, item1.getPrice());
        assertEquals(1, item1.getQuantity());
    }

    @Test
    void getTotalPriceTest() {
        Item item1 = new Item("Ballon Pack", 5.98, 2);
        Item item2 = new Item("Birthday Cake", 17.83, 1);
        Item item3 = new Item("OverCooked! 2", 35.99, 1);
        Item item4 = new Item("Birthday Bag 3 pack", 2.98, 1);

        ArrayList<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);

        PickupOrder order1 = new PickupOrder(1, 32726, items);
        assertEquals(68.76, order1.getTotalPrice());
    }

    @Test
    void orderReturnsItemsTest() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Peep Plush 12in", 7.23, 1));

        ShipOrder order1 = new ShipOrder(2, 229, items);

        assertEquals(1, order1.getItems().size());
        assertEquals("Peep Plush 12in", order1.getItems().getFirst().getName());
    }

    @Test
    void orderWithZeroItemsTest() {
        ArrayList<Item> items = new ArrayList<>();

        DeliveryOrder order1 = new DeliveryOrder(3, 823, items);
        assertEquals(0.0 ,order1.getTotalPrice());
    }

}
