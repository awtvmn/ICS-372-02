package edu.metrostate;

public class Item {
    private String name;
    private float price;

    //constructor
    public Item(String name, float price) {
        this.name = name;
        this.price = price;
    }

    //getters
    public String getName() {return name;}
    public float getPrice() {return price;}

    //setters
    public void setName(String name) {this.name = name;}
    public void setPrice(float price) {this.price = price;}
}
