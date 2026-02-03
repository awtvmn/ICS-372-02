package edu.metrostate;

public class Item {
    private String name;
    private float price;
    private int quantity;

    //constructor
    public Item(String name, float price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    //getters
    public String getName() {return name;}
    public float getPrice() {return price;}
    public int getQuantity() {return quantity;}

    //setters
    public void setName(String name) {this.name = name;}
    public void setPrice(float price) {this.price = price;}
    public void setQuantity(int quantity) {this.quantity = quantity;}
}
