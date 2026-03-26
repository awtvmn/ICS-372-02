package edu.metrostate;

import java.io.Serial;
import java.io.Serializable;

/**
 * Item class, used to create different item objects
 */
public class Item implements Serializable {
    private String name;
    private double price;
    private int quantity;
    @Serial
    private static final long serialVersionUID = 1L;

    //constructor
    public Item(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    //getters
    public String getName() {return name;}
    public double getPrice() {return price;}
    public int getQuantity() {return quantity;}

    //setters
    public void setName(String name) {this.name = name;}
    public void setPrice(double price) {this.price = price;}
    public void setQuantity(int quantity) {this.quantity = quantity;}

}
