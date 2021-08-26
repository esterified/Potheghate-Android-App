package com.example.potheghate.Model;

import android.net.Uri;

public class V_Data {
    private String name;
    private Integer price;
    private String subcatTitle;
    private String subcategory;
    private String resId;
    private String itemId;
    private String variantId;
    private Uri image;

    public V_Data() {
    }

    public V_Data(String name, Integer price, String subcatTitle, String subcategory, String resId, String itemId, String variantId, Uri image) {
        this.name = name;
        this.price = price;
        this.subcatTitle = subcatTitle;
        this.subcategory = subcategory;
        this.resId = resId;
        this.itemId = itemId;
        this.variantId = variantId;
        this.image = image;
    }

    public V_Data(String name, Integer price, String subcatTitle, String subcategory, String resId, String itemId, String variantId) {
        this.name = name;
        this.price = price;
        this.subcatTitle = subcatTitle;
        this.subcategory = subcategory;
        this.resId = resId;
        this.itemId = itemId;
        this.variantId = variantId;
    }

    public String getResId() {
        return resId;
    }

    public String getSubcategory() {
        return subcategory;
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

    public String getSubcatTitle() {
        return subcatTitle;
    }

    public String getItemId() {
        return itemId;
    }

    public String getVariantId() {
        return variantId;
    }

    public Uri getImage() {
        return image;
    }


}
