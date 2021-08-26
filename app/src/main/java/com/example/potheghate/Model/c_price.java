package com.example.potheghate.Model;

public class c_price {
    private String name;
    private Float price;

    public c_price(String name, Float price) {
        this.name = name;
        this.price = price;
    }

    public c_price() {
    }

    public String getName() {
        return name;
    }

    public Float getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
