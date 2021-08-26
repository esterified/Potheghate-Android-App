package com.example.potheghate.Model;

import com.example.potheghate.Interface.product_h2c;

public class c_data_to extends product_h2c{
    private String courier_service;
    private String receiver_details;
    private String courier_package;
    public c_data_to(String courier_service, String receiver_details, String courier_package, String product_weight,
                     String product_details, String product_quantity,String condition,Float condition_amount) {
        super(product_weight,product_details,product_quantity,condition,condition_amount);
        this.courier_service = courier_service;
        this.receiver_details = receiver_details;
        this.courier_package = courier_package;

    }
    public c_data_to(String courier_service, String receiver_details, String courier_package, String product_weight,
                     String product_details, String product_quantity, String condition) {
        super(product_weight,product_details,product_quantity,condition);
        this.courier_service = courier_service;
        this.receiver_details = receiver_details;
        this.courier_package = courier_package;

    }

    public c_data_to(String courier_service, String receiver_details, String courier_package) {
        this.courier_service = courier_service;
        this.receiver_details = receiver_details;
        this.courier_package = courier_package;

    }

    public c_data_to() {
    }
    public boolean equals(String courier_service){
    return courier_service.equals(this.courier_service);
    }


    public String getCourier_package() {
        return courier_package;
    }

    public String getCourier_service() {
        return courier_service;
    }

    public String getReceiver_details() {
        return receiver_details;
    }


}
