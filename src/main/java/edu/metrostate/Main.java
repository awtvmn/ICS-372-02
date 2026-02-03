package edu.metrostate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //FileReader fr = new FileReader(order.json);
        JSONParser parser = new JSONParser();
        Scanner s = new Scanner(System.in);

        try {
            //asking for file name
            System.out.println("Enter order file (txt/(filename).json): ");
            String filename = s.nextLine();
            File file = new File(filename);

            //checkin
            if(!file.exists()){
                System.out.println("File does not exist");
            }

            JSONObject js = (JSONObject)parser.parse(new FileReader(filename));
            System.out.println(js);
            //The software shall read and store the order type
            JSONObject order = (JSONObject) js.get("order");
            String type = (String)order.get("type");
            System.out.println("type is " + type);

            long order_date = (long)order.get("order_date");
            System.out.println("order date is  " + order_date);

            // the following parses from JSON requested items, quantity,and item price of each order
            JSONArray items = (JSONArray) order.get("items");
            for (Object obj : items){
                JSONObject item = (JSONObject) obj;
                String name = (String) item.get("name");
                long quantity = (long) item.get("quantity");
                double price = (double) item.get("price");

                System.out.println("item  " + name);
                System.out.println("quantity  " + quantity);
                System.out.println("price  " + price);

            }


        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}