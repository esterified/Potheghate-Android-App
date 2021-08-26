package com.example.potheghate.Model;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private String id;
    private String name;
    private String res_id;
    private String item_id;
    private boolean subcategory;
    private String quantity;
    private Integer price;
    private String image;
    private Integer totalprice;


    public Cart(String id, String name, String res_id, String item_id, boolean subcategory, String quantity, Integer price, String image) {
        this.id = id;
        this.name = name;
        this.res_id = res_id;
        this.item_id = item_id;
        this.subcategory = subcategory;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
        this.totalprice=price;
        this.calcTotalprice();

    }

    public Cart(String id, String name, String res_id, String item_id, boolean subcategory, String quantity, Integer price) {
        this.id = id;
        this.name = name;
        this.res_id = res_id;
        this.item_id = item_id;
        this.subcategory = subcategory;
        this.quantity = quantity;
        this.price = price;
        this.totalprice=price;
        this.calcTotalprice();
    }

    public Cart() {
    }

    public Integer getTotalprice() {
        return totalprice;
    }

    public void calcTotalprice(){
        Integer price=this.price;
        Integer q=Integer.parseInt(this.quantity);
        this.totalprice=price*q;

    }

    public void setTotalprice(Integer totalprice) {
        this.totalprice = totalprice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSubcategory() {
        return subcategory;
    }

    public void setSubcategory(boolean subcategory) {
        this.subcategory = subcategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRes_id() {
        return res_id;
    }

    public void setRes_id(String res_id) {
        this.res_id = res_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
