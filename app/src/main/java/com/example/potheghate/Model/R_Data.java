package com.example.potheghate.Model;

public class R_Data {
    private String name;
    private String location;
    private String id;
    private String open_at;
    private String close_at;
    private long itemCount;
    private String image;

    public R_Data(String name, String location, String id, String open_at, String close_at, long itemCount) {
        this.name = name;
        this.location = location;
        this.id = id;
        this.open_at = open_at;
        this.close_at = close_at;
        this.itemCount = itemCount;
    }

    public R_Data(String name, String location, String id, String open_at, String close_at, long itemCount, String image) {
        this.name = name;
        this.location = location;
        this.id = id;
        this.open_at = open_at;
        this.close_at = close_at;
        this.itemCount = itemCount;
        this.image = image;
    }

    public R_Data(){

    }

    public String getOpen_at() {
        return open_at;
    }

    public String getClose_at() {
        return close_at;
    }

    public long getItemCount() {
        return itemCount;
    }

    public void setItemCount(long itemCount) {
        this.itemCount = itemCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
