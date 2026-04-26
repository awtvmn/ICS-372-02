package edu.metrostate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * OrderImporter class - responsible for parsing order files and adding them to the OrderManager.
 *
 * Supports two file formats:
 *   - XML files via importXML()
 *   - JSON files via importJSON()
 *
 */
public class OrderImporter {

    /**
     * Reads an XML order file and adds orders to the OrderManager.
     * Skips orders that are invalid or missing fields.
     * Feature 3 - XML file importing.
     *
     * @param file         the XML file to import
     * @param orderManager the OrderManager to add parsed orders into
     * @param onImported   a callback to run after a successful import, or null if not needed
     */
    public static void importXML(File file, OrderManager orderManager, Runnable onImported) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList orderNodes = doc.getElementsByTagName("Order");
            int importedCount = 0;

            for (int i = 0; i < orderNodes.getLength(); i++) {
                Element orderElement = (Element) orderNodes.item(i);

                try {
                    // Read order attributes
                    int orderID = Integer.parseInt(orderElement.getAttribute("id"));
                    String type = getTagValue(orderElement, "OrderType", "pickup").toLowerCase();

                    ArrayList<Item> items = new ArrayList<>();

                    // Parse items
                    NodeList itemNodes = orderElement.getElementsByTagName("Item");
                    for (int j = 0; j < itemNodes.getLength(); j++) {
                        Element itemElement = (Element) itemNodes.item(j);
                        String name = itemElement.getAttribute("type");

                        if (name == null || name.isEmpty()) name = "Unknown Item";
                        double price = Double.parseDouble(getTagValue(itemElement, "Price", "0"));
                        int quantity = Integer.parseInt(getTagValue(itemElement, "Quantity", "1"));

                        items.add(new Item(name, price, quantity));
                    }

                    int sizeBefore = orderManager.getAllOrders().size();
                    if (orderManager.getAllOrders().containsKey(orderID)) {
                        System.out.println("    Order #" + orderID + " already exists. Reassigning to new ID.");
                    }
                    orderManager.addOrderWithID(orderID, type, items, file.getName());
                    if (orderManager.getAllOrders().size() > sizeBefore) {
                        importedCount++;
                    }

                } catch (Exception ex) {
                    System.out.println("    Skipping invalid order in file " + file.getName() + ": " + ex.getMessage());
                }
            }

            if (importedCount == 0) {
                System.out.println("--- No orders imported from " + file.getName() + " ---");
            } else {
                System.out.println("--- Successfully imported " + importedCount + " order(s) from " + file.getName() + " ---");
            }

            // Refresh GUI
            if (onImported != null) onImported.run();

        } catch (Exception e) {
            System.out.println("--- Failed to import XML: " + file.getName());
            System.out.println("    Reason: " + e.getMessage());
        }
    }

    /**
     * Reads a JSON order file and adds the order to the OrderManager.
     * Skips the file if it is invalid or missing required fields.
     *
     * @param file         the JSON file to import
     * @param orderManager the OrderManager to add the parsed order into
     * @param onImported   a callback to run after a successful import, or null if not needed
     */
    public static void importJSON(File file, OrderManager orderManager, Runnable onImported) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject js = (JSONObject) parser.parse(new FileReader(file));

            // Read the order object from the JSON file
            JSONObject order = (JSONObject) js.get("order");
            String type = (String) order.get("type");
            long orderDate = (long) order.get("order_date");

            // Parse all items in this order
            ArrayList<Item> items = new ArrayList<>();
            JSONArray itemsArray = (JSONArray) order.get("items");
            for (Object obj : itemsArray) {
                JSONObject item = (JSONObject) obj;
                String name = (String) item.get("name");
                long quantity = (long) item.get("quantity");
                double price = (double) item.get("price");
                items.add(new Item(name, price, (int) quantity));
            }

            OrderResult result = orderManager.addOrder(type, orderDate, items);

            // Set the source file on the newly added order
            orderManager.getAllOrders().values().stream()
                    .filter(o -> o.getSourceFile() == null && o.getOrderDate() == orderDate)
                    .findFirst()
                    .ifPresent(o -> o.setSourceFile(file.getName()));

            System.out.println("--- " + result.getMessage() + " from " + file.getName() + " ---");

            // Notify the GUI to refresh the order list
            if (onImported != null) onImported.run();

        } catch (Exception e) {
            System.out.println("--- Failed to import JSON: " + file.getName());
            System.out.println("    Reason: " + e.getMessage());
        }
    }

    /**
     * Returns the text content of a child element within a parent XML element.
     * Returns a default value if the tag is not found.
     *
     * @param parent       the parent XML element to search within
     * @param tagName      the name of the child tag to find
     * @param defaultValue the value to return if the tag is not found
     * @return the text content of the tag, or defaultValue if not found
     */
    private static String getTagValue(Element parent, String tagName, String defaultValue) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return defaultValue;
        return nodes.item(0).getTextContent();
    }
}