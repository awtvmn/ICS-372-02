package edu.metrostate;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Directory class.
 * Covers XML import, source file tracking, ID collision,
 * and invalid file handling.
 */
public class DirectoryTest {

    private OrderManager orderManager;
    private Directory directory;

    @BeforeEach
    void setUp() {
        new File("importedFiles.dat").delete();
        orderManager = new OrderManager();
        directory = new Directory(orderManager);
    }

    @AfterEach
    void tearDown() {
        new File("importedFiles.dat").delete();
        File folder = new File("watched");
        if (folder.exists()) {
            for (File f : folder.listFiles()) {
                if (f.getName().startsWith("test_")) f.delete();
            }
        }
    }

    private File writeXML(String fileName, String content) throws IOException {
        File folder = new File("watched");
        folder.mkdirs();
        File file = new File(folder, fileName);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
        return file;
    }

    @Test
    void importValidXMLSingleOrderTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="101">
                    <OrderType>delivery</OrderType>
                    <Item type="Rubber Duck">
                        <Price>13.45</Price>
                        <Quantity>2</Quantity>
                    </Item>
                </Order>
            </Orders>
            """;
        writeXML("test_valid.xml", xml);
        directory.checkFolder();
        assertTrue(orderManager.getAllOrders().containsKey(101));
        assertEquals("delivery", orderManager.getAllOrders().get(101).getType());
    }

    @Test
    void importValidXMLMultipleOrdersTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="201">
                    <OrderType>ship</OrderType>
                    <Item type="Soap"><Price>5.25</Price><Quantity>1</Quantity></Item>
                </Order>
                <Order id="202">
                    <OrderType>pickup</OrderType>
                    <Item type="Towel"><Price>8.99</Price><Quantity>3</Quantity></Item>
                </Order>
            </Orders>
            """;
        writeXML("test_multi.xml", xml);
        directory.checkFolder();
        assertEquals(2, orderManager.getAllOrders().size());
        assertTrue(orderManager.getAllOrders().containsKey(201));
        assertTrue(orderManager.getAllOrders().containsKey(202));
    }

    @Test
    void importXMLUnknownTypeSkippedTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="501">
                    <OrderType>teleport</OrderType>
                    <Item type="Magic Box"><Price>99.99</Price><Quantity>1</Quantity></Item>
                </Order>
            </Orders>
            """;
        writeXML("test_invalidtype.xml", xml);
        directory.checkFolder();
        assertFalse(orderManager.getAllOrders().containsKey(501));
        assertEquals(0, orderManager.getAllOrders().size());
    }

    @Test
    void importXMLBrokenSyntaxDoesNotCrashTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="301">
                    <OrderType>ship</OrderType>
                    <Item type="Broken">
                        <Price>10.00</Price>
                        <Quantity>1
                    </Item>
            """;
        writeXML("test_broken.xml", xml);
        assertDoesNotThrow(() -> directory.checkFolder());
        assertEquals(0, orderManager.getAllOrders().size());
    }

    @Test
    void sameFileNotImportedTwiceTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="401">
                    <OrderType>pickup</OrderType>
                    <Item type="Chair"><Price>199.99</Price><Quantity>1</Quantity></Item>
                </Order>
            </Orders>
            """;
        writeXML("test_duplicate.xml", xml);
        directory.checkFolder();
        directory.checkFolder();
        assertEquals(1, orderManager.getAllOrders().size());
    }

    @Test
    void idCollisionOrderReassignedTest() throws IOException {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Existing Item", 10.00, 1));
        orderManager.addOrderWithID(485, "ship", items, null);

        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="485">
                    <OrderType>delivery</OrderType>
                    <Item type="Rubber Duck"><Price>13.45</Price><Quantity>2</Quantity></Item>
                </Order>
            </Orders>
            """;
        writeXML("test_collision.xml", xml);
        directory.checkFolder();
        assertEquals(2, orderManager.getAllOrders().size());
        assertTrue(orderManager.getAllOrders().containsKey(485));
    }

    @Test
    void sourceFileIsSetTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="600">
                    <OrderType>ship</OrderType>
                    <Item type="Pen"><Price>2.99</Price><Quantity>5</Quantity></Item>
                </Order>
            </Orders>
            """;
        writeXML("test_sourcefile.xml", xml);
        directory.checkFolder();
        Order order = orderManager.getAllOrders().get(600);
        assertNotNull(order);
        assertEquals("test_sourcefile.xml", order.getSourceFile());
    }

    @Test
    void itemsParsedCorrectlyTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="700">
                    <OrderType>pickup</OrderType>
                    <Item type="Soap"><Price>5.25</Price><Quantity>2</Quantity></Item>
                    <Item type="Towel"><Price>8.99</Price><Quantity>1</Quantity></Item>
                </Order>
            </Orders>
            """;
        writeXML("test_items.xml", xml);
        directory.checkFolder();
        Order order = orderManager.getAllOrders().get(700);
        assertNotNull(order);
        assertEquals(2, order.getItems().size());
        assertEquals("Soap", order.getItems().get(0).getName());
        assertEquals(5.25, order.getItems().get(0).getPrice());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }

    @Test
    void partialImportValidAndInvalidOrderTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="801">
                    <OrderType>ship</OrderType>
                    <Item type="Charger"><Price>29.99</Price><Quantity>1</Quantity></Item>
                </Order>
                <Order id="802">
                    <OrderType>delivery</OrderType>
                    <Item type="Broken"><Price>not-a-number</Price><Quantity>1</Quantity></Item>
                </Order>
            </Orders>
            """;
        writeXML("test_partial.xml", xml);
        directory.checkFolder();
        assertEquals(1, orderManager.getAllOrders().size());
        assertTrue(orderManager.getAllOrders().containsKey(801));
        assertFalse(orderManager.getAllOrders().containsKey(802));
    }

    @Test
    void importedOrderStartsAsIncomingTest() throws IOException {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Orders>
                <Order id="900">
                    <OrderType>delivery</OrderType>
                    <Item type="Box"><Price>1.00</Price><Quantity>1</Quantity></Item>
                </Order>
            </Orders>
            """;
        writeXML("test_incoming.xml", xml);
        directory.checkFolder();
        Order order = orderManager.getAllOrders().get(900);
        assertNotNull(order);
        assertEquals(OrderStatus.INCOMING, order.getOrderStatus());
    }
}