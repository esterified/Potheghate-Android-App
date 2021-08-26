package com.example.potheghate.Model;

import android.net.Uri;

public class R_ItemData {
    private String image;
    private String name;
    private Integer price;
    private String id;
    private boolean subcategory;

    public R_ItemData(String name, Integer price, String image, String id, boolean subcategory) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.subcategory = subcategory;
    }



    public boolean isSubcategory() {
        return subcategory;
    }

    public void setSubcategory(boolean subcategory) {
        this.subcategory = subcategory;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
