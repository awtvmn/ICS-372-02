package edu.metrostate;

public class Item {
    private String name;
    private double price;
    private long quantity;

    //constructor
    public Item(String name, double price, long quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    //getters
    public String getName() {return name;}
    public double getPrice() {return price;}
    public long getQuantity() {return quantity;}

    //setters
    public void setName(String name) {this.name = name;}
    public void setPrice(float price) {this.price = price;}
    public void setQuantity(long quantity) {this.quantity = quantity;}
@Override
    public String toString(){
        return name + ": " + "quantity " + quantity  + " price is " + price ;
    }

}
