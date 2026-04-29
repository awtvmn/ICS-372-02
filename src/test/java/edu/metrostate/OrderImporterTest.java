package edu.metrostate;

import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OrderImporter class.
 * Covers XML and JSON parsing, invalid files, and source file tracking.
 */
public class OrderImporterTest {

    private OrderManager orderManager;
    private File testFolder;

    @BeforeEach
    void setUp() {
        new File("allOrders.dat").delete();
        orderManager = new OrderManager();
        testFolder = new File("importer_test");
        testFolder.mkdirs();
    }

    @AfterEach
    void tearDown() {
        new File("allOrders.dat").delete();
        if (testFolder.exists() && testFolder.listFiles() != null) {
            for (File f : testFolder.listFiles()) f.delete();
        }
        testFolder.delete();
    }

    /**
     * Helper to write an XML file into the test folder.
     */
    private File writeXML(String fileName, String content) throws IOException {
        File file = new File(testFolder, fileName);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
        return file;
    }

    /**
     * Helper to write a JSON file into the test folder.
     */
    private File writeJSON(String fileName, String content) throws IOException {
        File file = new File(testFolder, fileName);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
        return file;
    }

    @Test
    void importXMLAddsOrderToManagerTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="10">
                    <OrderType>ship</OrderType>
                    <Item type="Pen"><Price>2.99</Price><Quantity>3</Quantity></Item>
                </Order>
            </Orders>
            """;
        File file = writeXML("test_xml_add.xml", xml);
        OrderImporter.importXML(file, orderManager, null);

        assertTrue(orderManager.getAllOrders().containsKey(10));
        assertEquals("ship", orderManager.getAllOrders().get(10).getType());
    }

    @Test
    void importXMLSetsSourceFileTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="20">
                    <OrderType>pickup</OrderType>
                    <Item type="Notebook"><Price>5.00</Price><Quantity>1</Quantity></Item>
                </Order>
            </Orders>
            """;
        File file = writeXML("test_xml_source.xml", xml);
        OrderImporter.importXML(file, orderManager, null);

        assertEquals("test_xml_source.xml", orderManager.getAllOrders().get(20).getSourceFile());
    }

    @Test
    void importXMLSkipsUnknownOrderTypeTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="30">
                    <OrderType>teleport</OrderType>
                    <Item type="Box"><Price>1.00</Price><Quantity>1</Quantity></Item>
                </Order>
            </Orders>
            """;
        File file = writeXML("test_xml_unknown.xml", xml);
        OrderImporter.importXML(file, orderManager, null);

        assertEquals(0, orderManager.getAllOrders().size());
    }

    @Test
    void importXMLDoesNotCrashOnBrokenFileTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="40">
                    <OrderType>ship</OrderType>
                    <Item type="Box">
                        <Price>1.00</Price>
                        <Quantity>1
                    </Item>
            """;
        File file = writeXML("test_xml_broken.xml", xml);
        assertDoesNotThrow(() -> OrderImporter.importXML(file, orderManager, null));
        assertEquals(0, orderManager.getAllOrders().size());
    }

    @Test
    void importXMLParsesItemsCorrectlyTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="50">
                    <OrderType>delivery</OrderType>
                    <Item type="Soap"><Price>5.25</Price><Quantity>2</Quantity></Item>
                    <Item type="Towel"><Price>8.99</Price><Quantity>1</Quantity></Item>
                </Order>
            </Orders>
            """;
        File file = writeXML("test_xml_items.xml", xml);
        OrderImporter.importXML(file, orderManager, null);

        Order order = orderManager.getAllOrders().get(50);
        assertNotNull(order);
        assertEquals(2, order.getItems().size());
        assertEquals("Soap", order.getItems().get(0).getName());
        assertEquals(5.25, order.getItems().get(0).getPrice());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }

    @Test
    void importXMLOrderStartsAsIncomingTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="60">
                    <OrderType>ship</OrderType>
                    <Item type="Pen"><Price>1.00</Price><Quantity>1</Quantity></Item>
                </Order>
            </Orders>
            """;
        File file = writeXML("test_xml_incoming.xml", xml);
        OrderImporter.importXML(file, orderManager, null);

        assertEquals(OrderStatus.INCOMING, orderManager.getAllOrders().get(60).getOrderStatus());
    }

    @Test
    void importJSONAddsOrderToManagerTest() throws IOException {
        String json = """
            {
              "order": {
                "type": "pickup",
                "order_date": 1714000000,
                "items": [
                  { "name": "Widget", "quantity": 2, "price": 10.00 }
                ]
              }
            }
            """;
        File file = writeJSON("test_json_add.json", json);
        OrderImporter.importJSON(file, orderManager, null);

        assertEquals(1, orderManager.getAllOrders().size());
        assertTrue(orderManager.getAllOrders().values().stream()
                .anyMatch(o -> o.getType().equals("pickup")));
    }

    @Test
    void importJSONSetsSourceFileTest() throws IOException {
        String json = """
            {
              "order": {
                "type": "ship",
                "order_date": 1714000001,
                "items": [
                  { "name": "Box", "quantity": 1, "price": 5.00 }
                ]
              }
            }
            """;
        File file = writeJSON("test_json_source.json", json);
        OrderImporter.importJSON(file, orderManager, null);

        assertTrue(orderManager.getAllOrders().values().stream()
                .anyMatch(o -> "test_json_source.json".equals(o.getSourceFile())));
    }

    @Test
    void importJSONOrderStartsAsIncomingTest() throws IOException {
        String json = """
            {
              "order": {
                "type": "delivery",
                "order_date": 1714000002,
                "items": [
                  { "name": "Lamp", "quantity": 1, "price": 25.00 }
                ]
              }
            }
            """;
        File file = writeJSON("test_json_incoming.json", json);
        OrderImporter.importJSON(file, orderManager, null);

        assertTrue(orderManager.getAllOrders().values().stream()
                .allMatch(o -> o.getOrderStatus() == OrderStatus.INCOMING));
    }

    @Test
    void importJSONDoesNotCrashOnInvalidFileTest() throws IOException {
        String json = "{ invalid json content }";
        File file = writeJSON("test_json_broken.json", json);
        assertDoesNotThrow(() -> OrderImporter.importJSON(file, orderManager, null));
        assertEquals(0, orderManager.getAllOrders().size());
    }

    @Test
    void importJSONCallbackIsCalledTest() throws IOException {
        String json = """
            {
              "order": {
                "type": "ship",
                "order_date": 1714000003,
                "items": [
                  { "name": "Pen", "quantity": 1, "price": 1.50 }
                ]
              }
            }
            """;
        File file = writeJSON("test_json_callback.json", json);

        // Track whether the callback was called
        boolean[] callbackCalled = {false};
        OrderImporter.importJSON(file, orderManager, () -> callbackCalled[0] = true);

        assertTrue(callbackCalled[0]);
    }
}