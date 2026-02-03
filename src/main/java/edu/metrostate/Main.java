package edu.metrostate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //FileReader fr = new FileReader(order.json);
        JSONParser parser = new JSONParser();

        try {
            JSONObject js = (JSONObject)parser.parse(new FileReader("txt/order.json"));
            System.out.println(js);
            //The software shall read and store the order type
            JSONObject order = (JSONObject) js.get("order");
            String type = (String)order.get("type");
            System.out.println("type is " + type);

            String order_date = (String)order.get("order_date");
            System.out.println("order date is  " + order_date);


        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }
}