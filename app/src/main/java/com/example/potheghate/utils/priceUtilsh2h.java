package com.example.potheghate.utils;

import android.util.Log;

import com.example.potheghate.Model.c_price;
import com.example.potheghate.Model.h2h_data_from;

import java.util.ArrayList;
import java.util.HashMap;

public class priceUtilsh2h {

    public static final String MULTI_PERSON = "multi_person_h2h";
    public static final String COURIER_MATERIAL = "courier_material";
    public static final String OR_LESS = "5kg or less";
    public static final String H_2_H_BASE_PRICE = "h2h_base_price";
    private final HashMap<String, ArrayList<c_price>> price_all;
    public static final String TAG="utils";

    public priceUtilsh2h(HashMap<String, ArrayList<c_price>> price_all) {
        this.price_all = price_all;

    }

    public float calcPrice(ArrayList<h2h_data_from> data_f, ArrayList<h2h_data_from> data_t) {
        float pr = 0.0f;
        boolean flag_weight = false;
        if (data_t.size() > 1) {
            for (h2h_data_from a : data_t) {
                pr += priceLoop(data_t.indexOf(a), a);
                if (weightDiscount(a.getProduct_weight())) {
                    flag_weight = true;
                }
            }
        } else {
            for (h2h_data_from a : data_f) {
                pr += priceLoop(data_f.indexOf(a), a);
                if (weightDiscount(a.getProduct_weight())) {
                    flag_weight = true;
                }

            }
        }
        if (flag_weight) {
            pr -= 10.0f;
        }
        return pr;
    }

    private boolean weightDiscount(String weight) {
        boolean pri = false;
        if (!weight.equals(OR_LESS)) {
            pri = true;
        }
        return pri;
    }


    private float priceLoop(int index, h2h_data_from a) {
        float temp = price_all.get(H_2_H_BASE_PRICE).get(0).getPrice();//base_price
        float multi_person_price = price_all.get(MULTI_PERSON).get(0).getPrice();
        if (index > 0) {
            temp = multi_person_price;
        }
        temp += calcMaterialPrice(a.getProduct_weight());
        if(a.getCondition()!=null && a.getCondition().equals("Yes")){
        temp=temp+a.getProduct_condition_amount();
        }
        return temp;
    }


    public float calcMaterialPrice(String weight) {
        float pri = price_all.get(COURIER_MATERIAL).get(0).getPrice();
        for (c_price p : price_all.get(COURIER_MATERIAL)) {
            if (weight.equals(p.getName())) {
                pri = p.getPrice();
                break;
            }
        }
        return pri;
    }


}
