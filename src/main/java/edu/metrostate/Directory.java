package edu.metrostate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.nio.file.*;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Directory class - Feature 5
 * Checks the "watched/" folder every 3 seconds for new order files.
 * Automatically imports any new .json or .xml files it finds.
 */
public class Directory {
    private static final String watchFolder = "watched";
    private final OrderManager orderManager;
    private ArrayList<String> importedFiles = new ArrayList<>();

    private Runnable onOrderImported;


    public Directory(OrderManager orderManager) {
        this.orderManager = orderManager;
        File folder = new File(watchFolder);
        if(!folder.exists()) {
            folder.mkdir();
            System.out.println("Created watched/ folder at: " + folder.getAbsolutePath());
        }
        System.out.println("Watching folder:" + folder.getAbsolutePath());
    }

    public void setOnOrderImported(Runnable callback) {
        this.onOrderImported = callback;
    }


    /**
     * checkFolder - checks the watched/ folder for new files.
     * Called every 3 seconds from MainGUI.
     */
    public void checkFolder() {
        File folder = new File(watchFolder);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) return;

        for (File file : files) {
            String fileName = file.getName().toLowerCase();

            if (file.length() == 0) continue;

            if (importedFiles.contains(fileName)) continue;

            if (fileName.endsWith(".json")) {
                System.out.println("[Watcher] Found JSON file: " + fileName);
                importJSON(file);
                importedFiles.add(fileName);

            } else if (fileName.endsWith(".xml")) {
                System.out.println("[Watcher] Found XML file: " + fileName);
                importXML(file);
                importedFiles.add(fileName);

            } else {
                System.out.println("[Watcher] Unsupported file: " + fileName);
                importedFiles.add(fileName);
            }

        }

    }

    /**
     * importJSON - reads a JSON order file and adds it to the OrderManager
     */
    private void importJSON(File file) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject js = (JSONObject) parser.parse(new FileReader(file));
            JSONObject order = (JSONObject) js.get("order");

            String type = (String) order.get("type");
            long orderDate = (long) order.get("order_date");

            ArrayList<Item> items = new ArrayList<>();
            JSONArray itemsArray = (JSONArray) order.get("items");

            for (Object obj : itemsArray) {
                JSONObject item = (JSONObject) obj;
                String name = (String) item.get("name");
                long quantity = (long) item.get("quantity");
                double price = (double) item.get("price");
                items.add(new Item(name, price, (int) quantity));
            }

            orderManager.addOrder(type, orderDate, items);
            System.out.println("[Watcher] Successfully imported JSON: " + file.getName());

            if (onOrderImported != null) onOrderImported.run();

        } catch (Exception e) {
            System.out.println("[Watcher] Failed to import JSON: " + file.getName());
            System.out.println("[Watcher] Reason: " + e.getMessage());
        }
    }
    /**
     * importXML - reads a XML order file and adds it to the OrderManager
     */
    private void importXML(File file) {
        try {
            // Set up XML parser
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
                    int id = Integer.parseInt(orderElement.getAttribute("id"));
                    String type = getTagValue(orderElement, "OrderType", "pickup").toLowerCase();
                    long date = System.currentTimeMillis();

                    ArrayList<Item> items = new ArrayList<>();

                    // Parse items
                    NodeList itemNodes = orderElement.getElementsByTagName("Item");
                    for (int j = 0; j < itemNodes.getLength(); j++) {
                        Element itemElement = (Element) itemNodes.item(j);
                        String name;
                        name = itemElement.getAttribute("type");

                        if (name == null || name.isEmpty()) name = "Unknown Item";
                        double price = Double.parseDouble(getTagValue(itemElement, "Price", "0"));
                        int quantity = Integer.parseInt(getTagValue(itemElement, "Quantity", "1"));

                        items.add(new Item(name, price, quantity));
                    }

                    // Add order to OrderManager
                    orderManager.addOrder(type, date, items);

                    importedCount++;


                } catch (Exception ex) {
                    System.out.println("[Watcher] Skipping invalid order in file " + file.getName() + ": " + ex.getMessage());
                }
            }

            if (onOrderImported != null) onOrderImported.run();

            System.out.println("[Watcher] Successfully imported " + importedCount + " orders from XML: " + file.getName());

            if (onOrderImported != null) onOrderImported.run();

        } catch (Exception e) {
            System.out.println("[Watcher] Failed to import XML: " + file.getName());
            System.out.println("[Watcher] Reason: " + e.getMessage());
        }
    }

    // Helper method to get XML tag text
    private String getTagValue(Element parent, String tagName, String defaultValue) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return defaultValue;
        return nodes.item(0).getTextContent();
    }
}



