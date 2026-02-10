package edu.metrostate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        OrderManager manager = new OrderManager();
        //FileReader fr = new FileReader(order.json);
        JSONParser parser = new JSONParser();
        Scanner s = new Scanner(System.in);

        while (true) {
            System.out.println("1. Load order from file");
            System.out.println("2. Start order");
            System.out.println("3. Display order");
            System.out.println("4. Complete order");
            System.out.println("5. Show uncompleted orders");
            System.out.println("6. Export all order");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            int choice = s.nextInt();
            s.nextLine();
            if (choice == 1) {
                // Requirement 1: Read JSON file
                try {
                    //asking for file name
                    System.out.println("Enter order file (txt/(filename).json): ");
                    String filename = s.nextLine();
                    File file = new File(filename);

                    //checkin
                    if (!file.exists()) {
                        System.out.println("File does not exist");
                    }

                    JSONObject js = (JSONObject) parser.parse(new FileReader(filename));
                    System.out.println(js);
                    //The software shall read and store the order type
                    JSONObject order = (JSONObject) js.get("order");
                    String type = (String) order.get("type");
                    System.out.println("type is " + type);

                    long order_date = (long) order.get("order_date");
                    System.out.println("order date is  " + order_date);

                    // the following parses from JSON requested items, quantity,and item price of each order
                    ArrayList<Item> items = new ArrayList<>();
                    JSONArray itemsArray = (JSONArray) order.get("items");
                    for (Object obj : itemsArray) {
                        JSONObject item = (JSONObject) obj;
                        String name = (String) item.get("name");
                        long quantity = (long) item.get("quantity");
                        double price = (double) item.get("price");

                        items.add(new Item(name, price, (int) quantity));

                        System.out.println("item  " + name);
                        System.out.println("quantity  " + quantity);
                        System.out.println("price  " + price);

                    }

                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }

            } else if (choice == 2) {
                System.out.print("Enter order ID: ");
                int orderID = s.nextInt();
                manager.startOrder(orderID);

            } else if (choice == 3) {
                System.out.print("Enter order ID: ");
                int orderID = s.nextInt();
                manager.displayOrder(orderID);

            } else if (choice == 4) {
                System.out.print("Enter order ID: ");
                int orderID = s.nextInt();
                manager.completeOrder(orderID);

            } else if (choice == 5) { //incomplete

            } else if (choice == 6) { //incomplete

            } else if (choice == 7) {
                System.out.println("Goodbye");
                s.close();
                return;

            } else {
                System.out.println("Invalid choice.");
            }

        }
    }
}
