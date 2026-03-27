package edu.metrostate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.ArrayList;

/**
 * Directory class - Feature 5.
 * Checks the "watched/" folder every 3 seconds for new order files.
 * Automatically imports any new .xml files it finds.
 * Feature 5
 */
public class Directory implements Serializable {
    private static final String watchFolder = "watched";
    private final OrderManager orderManager;
    private ArrayList<String> importedFiles;
    @Serial
    private static final long serialVersionUID = 1L;

    private Runnable onOrderImported;


    /**
     * Constructs a Directory watcher.
     * Loads the list of previously imported files from disk
     * Creates the watched/ folder if it does not already exist.
     *
     * @param orderManager the OrderManager to add imported orders into
     */
    public Directory(OrderManager orderManager) {
        this.orderManager = orderManager;

        try (ObjectInputStream temp = new ObjectInputStream( new FileInputStream("importedFiles.dat"))) {
            importedFiles = (ArrayList<String>) temp.readObject();
        } catch (Exception e) {
            importedFiles = new ArrayList<>();
        }

        File folder = new File(watchFolder);
        if(!folder.exists()) {
            folder.mkdir();
            System.out.println("Created watched/ folder at: " + folder.getAbsolutePath());
        }
        System.out.println("Watching folder:" + folder.getAbsolutePath());
    }

    /**
     * Sets the callback to run after a new order is successfully imported.
     * Used by MainGUI to refresh the order list when a file is detected.
     *
     * @param callback the Runnable to call after a successful import
     */
    public void setOnOrderImported(Runnable callback) {
        this.onOrderImported = callback;
    }


    /**
     * Checks the watched/ folder for new files.
     * Called every 3 seconds from MainGUI
     * Skips empty files and files that have already been imported.
     * Supports .xml files only - Feature 3
     */
    public void checkFolder() {
        File folder = new File(watchFolder);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) return;

        for (File file : files) {
            String fileName = file.getName().toLowerCase();

            if (file.length() == 0) continue;
            if (importedFiles.contains(fileName)) continue;

            if (fileName.endsWith(".xml")) {
                System.out.println("\n--- Importing: " + fileName + " ---");
                importXML(file);
                importedFiles.add(fileName);
                saveImportedFiles();
            } else {
                System.out.println("\n--- Unsupported file: " + fileName);
                importedFiles.add(fileName);
                saveImportedFiles();
            }

        }

    }

    /**
     * Saves the list of imported file names to disk.
     * Ensures already-imported files are not re-imported on the next session.
     * Part of Feature 2
     * Note: to reset, delete both allOrders.dat and importedFiles.dat.
     */
    public void saveImportedFiles() {
        try (ObjectOutputStream temp = new ObjectOutputStream( new FileOutputStream("importedFiles.dat"))) {
            temp.writeObject(importedFiles);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Reads an XML order file and adds orders to the OrderManager.
     * Skips order that are invalid or missing fields
     * @param file the XML file to import
     */
    private void importXML(File file) {
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
                    orderManager.addOrderWithID(orderID, type, items, file.getName());
                    if (orderManager.getAllOrders().size() > sizeBefore) {
                        importedCount++;}



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
            if (onOrderImported != null) onOrderImported.run();

        } catch (Exception e) {
            System.out.println("--- Failed to import XML: " + file.getName());
            System.out.println("    Reason: " + e.getMessage());
        }
    }

    /**
     * Returns the text content of a child element within a parent element.
     * Returns a default value if the tag is not found.
     *
     * @param parent       the parent XML element to search within
     * @param tagName      the name of the child tag to find
     * @param defaultValue the value to return if the tag is not found
     * @return the text content of the tag, or defaultValue if not found
     */
    private String getTagValue(Element parent, String tagName, String defaultValue) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return defaultValue;
        return nodes.item(0).getTextContent();
    }
}



