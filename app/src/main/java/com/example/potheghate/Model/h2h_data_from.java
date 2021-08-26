package com.example.potheghate.Model;

import com.example.potheghate.Interface.product_h2c;
import com.example.potheghate.Interface.product_h2h;

public class h2h_data_from extends product_h2h {
    private String name;
    private String phone;
    private String address;


    public h2h_data_from(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
    public h2h_data_from(String name, String phone, String address,String product_weight, String product_details, String condition, Float product_condition_amount, String product_condition_payer) {
        super(product_weight,product_details,condition,product_condition_amount,product_condition_payer);
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
    public h2h_data_from(String name, String phone, String address,String product_weight, String product_details, String condition) {
        super(product_weight,product_details,condition);
        this.name = name;
        this.phone = phone;
        this.address = address;

    }


    public h2h_data_from() {
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
