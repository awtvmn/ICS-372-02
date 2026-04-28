package edu.metrostate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * OrderExporter class - responsible for exporting orders to external files.
 *
 * Supports two file formats:
 *   - XML files via exportXML()
 *   - JSON files via exportJSON()
 */
public class OrderExporter {

    /**
     * Exports all orders to an XML file in the "exported xml files" folder.
     *
     * @param orders the map of orders to export
     */
    public static void exportXML(HashMap<Integer, Order> orders) {
        File folder = new File("exported xml files");
        if (!folder.exists()) folder.mkdir();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Orders>\n");

        for (Order order : orders.values()) {
            xml.append("    <Order id=\"").append(order.getOrderID()).append("\">\n");
            xml.append("        <OrderType>").append(order.getType()).append("</OrderType>\n");
            xml.append("        <Status>").append(order.getOrderStatus()).append("</Status>\n");

            if (order.getOrderStatus() != OrderStatus.COMPLETED) {
                xml.append("        <PriceTotal>").append(order.getTotalPrice()).append("</PriceTotal>\n");
            }

            if (order.getSourceFile() != null) {
                xml.append("        <SourceFile>").append(order.getSourceFile()).append("</SourceFile>\n");
            }

            for (Item item : order.getItems()) {
                xml.append("        <Item type=\"").append(item.getName()).append("\">\n");
                xml.append("            <Price>").append(item.getPrice()).append("</Price>\n");
                xml.append("            <Quantity>").append(item.getQuantity()).append("</Quantity>\n");
                xml.append("        </Item>\n");
            }

            xml.append("    </Order>\n");
        }

        xml.append("</Orders>");

        try (FileWriter writer = new FileWriter("exported xml files/all-orders.xml")) {
            writer.write(xml.toString());
            System.out.println("All orders exported to XML successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exports all orders to a JSON file in the "exported json files" folder.
     *
     * @param orders the map of orders to export
     */
    public static void exportJSON(HashMap<Integer, Order> orders) {
        File folder = new File("exported json files");
        if (!folder.exists()) folder.mkdir();

        JSONObject root = new JSONObject();
        JSONArray ordersArray = new JSONArray();

        for (Order order : orders.values()) {
            JSONObject orderJson = new JSONObject();
            orderJson.put("order_id", order.getOrderID());
            orderJson.put("type", order.getType());
            orderJson.put("order_date", order.getOrderDate());
            orderJson.put("status", order.getOrderStatus());
            if (!order.getOrderStatus().toString().equals("COMPLETED")) {
                orderJson.put("price total", order.getTotalPrice());
            }

            JSONArray itemsArray = new JSONArray();
            for (Item item : order.getItems()) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("name", item.getName());
                itemJson.put("Quantity", item.getQuantity());
                itemJson.put("price", item.getPrice());
                itemsArray.add(itemJson);
            }
            orderJson.put("items", itemsArray);
            ordersArray.add(orderJson);
        }

        root.put("orders", ordersArray);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(root);

        try (FileWriter writer = new FileWriter("exported json files/all-orders.json")) {
            writer.write(prettyJson);
            System.out.println("All orders exported to JSON successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
