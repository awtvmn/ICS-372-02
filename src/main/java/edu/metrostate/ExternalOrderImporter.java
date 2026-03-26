package edu.metrostate;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.FileChooser;

public class ExternalOrderImporter {
    private OrderManager orderManager;

    public ExternalOrderImporter(OrderManager manager) {
        this.orderManager = manager;
    }


    public List<Order> importOrders(File xmlFile) {
        List<Order> importedOrders = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList orderNodes = doc.getElementsByTagName("Order");
            for (int i = 0; i < orderNodes.getLength(); i++) {
                Node orderNode = orderNodes.item(i);
                if (orderNode.getNodeType() != Node.ELEMENT_NODE) continue;

                Element orderElement = (Element) orderNode;
                try {
                    int id = Integer.parseInt(orderElement.getAttribute("id"));
                    String type = getTagValue(orderElement, "OrderType", "pickup"); // default pickup
                    long date = System.currentTimeMillis();

                    ArrayList<Item> items = new ArrayList<>();
                    Order order;

                    // Instantiate correct subclass based on type
                    switch (type.toLowerCase()) {
                        case "delivery":
                            order = new DeliveryOrder(id, date, items);
                            break;
                        case "ship":
                            order = new ShipOrder(id, date, items);
                            break;
                        case "pickup":
                        default:
                            order = new PickupOrder(id, date, items);
                            break;
                    }                    order.setSourceFile(xmlFile.getName()); // Track source

                    // Parse items
                    NodeList itemNodes = orderElement.getElementsByTagName("Item");
                    for (int j = 0; j < itemNodes.getLength(); j++) {
                        Node itemNode = itemNodes.item(j);
                        if (itemNode.getNodeType() != Node.ELEMENT_NODE) continue;

                        Element itemElement = (Element) itemNode;
                        String name = itemElement.getAttribute("type");
                        double price = Double.parseDouble(getTagValue(itemElement, "Price", "0"));
                        int quantity = Integer.parseInt(getTagValue(itemElement, "Quantity", "1"));

                        Item item = new Item(name, price, quantity);
                        order.add(item);
                    }

                    // Add order to OrderManager
                    orderManager.addOrder(type, date, items);
                    importedOrders.add(order);

                } catch (Exception ex) {
                    System.out.println("Skipping invalid order in file " + xmlFile.getName() + ": " + ex.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Failed to import orders from " + xmlFile.getName() + ": " + e.getMessage());
        }

        return importedOrders;
    }

    private String getTagValue(Element parent, String tagName, String defaultValue) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return defaultValue;
        return nodes.item(0).getTextContent();
    }
}

