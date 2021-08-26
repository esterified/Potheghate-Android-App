package com.example.potheghate.Interface;

public class product_h2h {
    public String product_weight;
    public String product_details;
    public String condition;
    public Float product_condition_amount;
    public String product_condition_payer;

    public product_h2h(String product_weight, String product_details, String condition, Float product_condition_amount, String product_condition_payer) {
        this.product_weight = product_weight;
        this.product_details = product_details;
        this.condition = condition;
        this.product_condition_amount = product_condition_amount;
        this.product_condition_payer = product_condition_payer;
    }

    public product_h2h(String product_weight, String product_details, String condition) {
        this.product_weight = product_weight;
        this.product_details = product_details;
        this.condition = condition;
    }

    public product_h2h() {
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

    public String getCondition() {
        if(condition!=null){
            return condition;
        }
        else{
            return null;
        }
    }

    public Float getProduct_condition_amount() {
        if(product_condition_amount!=null){
            return product_condition_amount;
        }
        else{
            return null;
        }
    }

    public String getProduct_condition_payer() {
        if(product_condition_payer!=null){
            return product_condition_payer;
        }
        else{
            return null;
        }
    }
}
