package edu.metrostate;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

    /**
     * Unit tests for the OrderManager class.
     * a
     */
    public class OrderManagerTest {

        private OrderManager orderManager;

        @BeforeEach
        void setUp() {
            // Remove any saved orders to start fresh
            new File("allOrders.dat").delete();
            orderManager = new OrderManager();
        }

        @AfterEach
        void tearDown() {
            new File("allOrders.dat").delete();
            File jsonFolder = new File("exported json files");
            if (jsonFolder.exists()) for (File f : jsonFolder.listFiles()) f.delete();
            File xmlFolder = new File("exported xml files");
            if (xmlFolder.exists()) for (File f : xmlFolder.listFiles()) f.delete();
        }

        private ArrayList<Item> createSampleItems() {
            ArrayList<Item> items = new ArrayList<>();
            items.add(new Item("Soap", 5.25, 2));
            items.add(new Item("Towel", 8.99, 1));
            return items;
        }

        @Test
        void addOrderCreatesCorrectOrderType() {
            ArrayList<Item> items = createSampleItems();
            orderManager.addOrder("ship", System.currentTimeMillis(), items);
            orderManager.addOrder("pickup", System.currentTimeMillis(), items);
            orderManager.addOrder("delivery", System.currentTimeMillis(), items);

            HashMap<Integer, Order> orders = orderManager.getAllOrders();
            assertEquals(3, orders.size());
            assertEquals("ship", orders.get(1).getType());
            assertEquals("pickup", orders.get(2).getType());
            assertEquals("delivery", orders.get(3).getType());
        }

        @Test
        void addOrderWithDuplicateIDReassignsNewID() {
            ArrayList<Item> items = createSampleItems();
            orderManager.addOrderWithID(1, "ship", items, "file1.xml");
            orderManager.addOrderWithID(1, "pickup", items, "file2.xml"); // duplicate ID

            HashMap<Integer, Order> orders = orderManager.getAllOrders();
            assertEquals(2, orders.size());
            assertTrue(orders.containsKey(1));
            assertTrue(orders.containsKey(2)); // new ID assigned
            assertEquals("file1.xml", orders.get(1).getSourceFile());
            assertEquals("file2.xml", orders.get(2).getSourceFile());
        }

        @Test
        void startOrderChangesStatus() {
            ArrayList<Item> items = createSampleItems();
            orderManager.addOrder("ship", 0, items);
            orderManager.startOrder(1);

            Order order = orderManager.getAllOrders().get(1);
            assertEquals(OrderStatus.IN_PROGRESS, order.getOrderStatus());
        }

        @Test
        void completeOrderOnlyCompletesInProgress() {
            ArrayList<Item> items = createSampleItems();
            orderManager.addOrder("ship", 0, items);
            orderManager.completeOrder(1); // cannot complete INCOMING
            assertEquals(OrderStatus.INCOMING, orderManager.getAllOrders().get(1).getOrderStatus());

            orderManager.startOrder(1);
            orderManager.completeOrder(1); // now can complete
            assertEquals(OrderStatus.COMPLETED, orderManager.getAllOrders().get(1).getOrderStatus());
        }

        @Test
        void cancelOrderWorks() {
            ArrayList<Item> items = createSampleItems();
            orderManager.addOrder("ship", 0, items);

            // Cancel INCOMING
            orderManager.cancelOrder(1);
            assertEquals(OrderStatus.CANCELED, orderManager.getAllOrders().get(1).getOrderStatus());

            // Cancel COMPLETED should not change
            orderManager.addOrder("ship", 0, items);
            orderManager.startOrder(2);
            orderManager.completeOrder(2);
            orderManager.cancelOrder(2);
            assertEquals(OrderStatus.COMPLETED, orderManager.getAllOrders().get(2).getOrderStatus());
        }

        @Test
        void exportOrdersCreatesJsonFile() throws IOException {
            ArrayList<Item> items = createSampleItems();
            orderManager.addOrder("ship", 0, items);
            orderManager.exportOrders();

            File jsonFile = new File("exported json files/all-orders.json");
            assertTrue(jsonFile.exists());
            assertTrue(jsonFile.length() > 0);
        }

        @Test
        void exportXMLCreatesXmlFile() throws IOException {
            ArrayList<Item> items = createSampleItems();
            orderManager.addOrder("pickup", 0, items);
            orderManager.exportXML();

            File xmlFile = new File("exported xml files/all-orders.xml");
            assertTrue(xmlFile.exists());
            assertTrue(xmlFile.length() > 0);
        }

        @Test
        void saveAndLoadOrdersPersistData() {
            ArrayList<Item> items = createSampleItems();
            orderManager.addOrder("delivery", 0, items);
            orderManager.saveOrder();

            OrderManager loadedManager = new OrderManager();
            loadedManager.loadOrders();
            HashMap<Integer, Order> orders = loadedManager.getAllOrders();

            assertEquals(1, orders.size());
            assertEquals("delivery", orders.get(1).getType());
        }

        @Test
        void unknownOrderTypeDoesNotAddOrder() {
            ArrayList<Item> items = createSampleItems();
            orderManager.addOrder("teleport", 0, items);
            assertEquals(0, orderManager.getAllOrders().size());
        }
    }

