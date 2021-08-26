package com.example.potheghate.Interface;

public class product_h2c {
    public String product_weight;
    public String product_details;
    public String product_quantity;
    public String condition;
    public Float condition_amount;

    public product_h2c(String product_weight, String product_details, String product_quantity, String condition) {
        this.product_weight = product_weight;
        this.product_details = product_details;
        this.product_quantity = product_quantity;
        this.condition = condition;
    }

    public product_h2c(String product_weight, String product_details, String product_quantity, String condition, Float condition_amount) {
        this.product_weight = product_weight;
        this.product_details = product_details;
        this.product_quantity = product_quantity;
        this.condition = condition;
        this.condition_amount = condition_amount;
    }

    public product_h2c() {
    }

    public String getCondition() {
        if(condition!=null){
            return condition;
        }
        else{
            return null;
        }
    }

    public String getProduct_weight() {
        if(product_weight!=null){
            return product_weight;
        }
        else{
            return null;
        }

    }

    public String getProduct_details() {
        if(product_details!=null){
            return product_details;
        }
        else{
            return null;
        }
    }

    public String getProduct_quantity() {
        if(product_quantity!=null){
            return product_quantity;
        }
        else{
            return null;
        }
    }
    public Float getCondtion_amount() {
        if(condition_amount!=null){
            return condition_amount;
        }
        else{
            return null;
        }
    }
}
