package com.example.potheghate.Model;

public class admin_o_data {
    private String time;
    private String type;
    private String payment_method;
    private Float price;

    public admin_o_data() {
    }

    public admin_o_data(String time, String type, String payment_method, Float price) {
        this.time = time;
        this.type = type;
        this.payment_method = payment_method;
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public Float getPrice() {
        return price;
    }
}
