package edu.metrostate;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OrderExporter class.
 * Covers XML and JSON file creation.
 */
public class OrderExporterTest {

    private HashMap<Integer, Order> orders;

    @BeforeEach
    void setUp() {
        orders = new HashMap<>();
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Soap", 5.25, 2));
        ShipOrder order = new ShipOrder(1, 0, items);
        orders.put(1, order);
    }

    @AfterEach
    void tearDown() {
        File jsonFolder = new File("exported json files");
        if (jsonFolder.exists() && jsonFolder.listFiles() != null)
            for (File f : jsonFolder.listFiles()) f.delete();
        File xmlFolder = new File("exported xml files");
        if (xmlFolder.exists() && xmlFolder.listFiles() != null)
            for (File f : xmlFolder.listFiles()) f.delete();
    }

    @Test
    void exportXMLCreatesFileTest() {
        OrderExporter.exportXML(orders);
        assertTrue(new File("exported xml files/all-orders.xml").exists());
    }

    @Test
    void exportJSONCreatesFileTest() {
        OrderExporter.exportJSON(orders);
        assertTrue(new File("exported json files/all-orders.json").exists());
    }

    @Test
    void exportXMLCompletedOrderHasNoPriceTotalTest() throws IOException {
        ShipOrder completed = new ShipOrder(2, 0, new ArrayList<>());
        completed.startFulfilling();
        completed.completeOrder();
        orders.put(2, completed);

        OrderExporter.exportXML(orders);
        String content = new String(new FileInputStream("exported xml files/all-orders.xml").readAllBytes());
        long count = content.lines().filter(l -> l.contains("<PriceTotal>")).count();
        assertEquals(1, count); // only the incomplete order should have it
    }
}