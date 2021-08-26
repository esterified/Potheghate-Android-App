package com.example.potheghate.Model;

import com.example.potheghate.Interface.product_h2c;

public class c_data_from extends product_h2c {
    private String name;
    private String phone;
    private String address;
    public c_data_from(String name, String phone, String address, String product_weight, String product_details, String product_quantity, String condition,Float condition_amount) {
        super(product_weight,product_details,product_quantity,condition,condition_amount);
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
    public c_data_from(String name, String phone, String address, String product_weight, String product_details, String product_quantity, String condition) {
        super(product_weight,product_details,product_quantity,condition);
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public c_data_from(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public c_data_from() {
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }


}
