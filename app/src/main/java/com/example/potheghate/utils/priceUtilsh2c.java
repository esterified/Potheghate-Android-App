package com.example.potheghate.utils;

import com.example.potheghate.Model.c_data_from;
import com.example.potheghate.Model.c_data_to;
import com.example.potheghate.Model.c_price;

import java.util.ArrayList;
import java.util.HashMap;

public class priceUtilsh2c {
    public static final String COURIER_PRICE = "courier_price";
    public static final String MULTI_PERSON = "multi_person";
    public static final String COURIER_MATERIAL = "courier_material";
    public static final String MULTI_PRODUCT = "multi_product";
    public static final String MULTI_COURIER = "multi_courier";
    public static final String OR_LESS = "5kg or less";
    private final HashMap<String, ArrayList<c_price>> price_all;
    private final HashMap<String, ArrayList<c_price>> price_all_courier;

    public priceUtilsh2c(HashMap<String, ArrayList<c_price>> price_all, HashMap<String, ArrayList<c_price>> price_all_courier) {
        this.price_all = price_all;
        this.price_all_courier = price_all_courier;
    }

    public float calcPrice(ArrayList<c_data_from> data_f, ArrayList<c_data_to> data_t) {
        float pr = 0.0f;
        boolean flag_weight = false;
        if (data_t.size() > 1) {
            for (c_data_to a : data_t) {
                pr += priceLoop(binning(data_t).get(data_t.indexOf(a)), a);
                if (weightDiscount(a.getProduct_weight())) {
                    flag_weight = true;
                }
            }
        } else {
            for (c_data_from a : data_f) {
                pr += priceLoop(data_f.indexOf(a), a, data_t.get(0));
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

    private float priceLoop(int index, c_data_from a, c_data_to t) {
        float temp = price_all.get(COURIER_PRICE).get(0).getPrice();//base_price
        float multi_person_price = price_all.get(MULTI_PERSON).get(0).getPrice();
        if (index > 0) {
            temp = multi_person_price;
        }
        else{
            temp += calcPackagePrice(t.getCourier_package(), t.getCourier_service());
        }
        temp += calcMaterialPrice(a.getProduct_weight());
        return temp;
    }

    private float priceLoop(float base_pr, c_data_to t) {
        float temp = base_pr;
        temp += calcMaterialPrice(t.getProduct_weight());
        temp += calcPackagePrice(t.getCourier_package(), t.getCourier_service());
        return temp;
    }

    public float calcPackagePrice(String pack, String courier_name) {
        float pri = price_all.get(COURIER_PRICE).get(0).getPrice();
        for (c_price p : price_all_courier.get(courier_name)) {
            if (pack.equals(p.getName())) {
                pri = p.getPrice();
                break;
            }
        }
        return pri;
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

    public ArrayList<Float> binning(ArrayList<c_data_to> data_t) {
        //------------binning--------------------------------
        float multi_product_price = price_all.get(MULTI_PRODUCT).get(0).getPrice();
        float multi_courier_price = price_all.get(MULTI_COURIER).get(0).getPrice();
        float courier_price = price_all.get(COURIER_PRICE).get(0).getPrice();
        HashMap<String, Integer> courier = new HashMap<>();
        ArrayList<Float> price_list = new ArrayList<>();
        boolean bool = true;
        for (c_data_to a : data_t) {
            if (courier.containsKey(a.getCourier_service())) {
                courier.put(a.getCourier_service(), courier.get(a.getCourier_service()) + 1);
                // courier_index.get(a.getCourier_service()).add(data_t.indexOf(a));
                price_list.add(multi_product_price);

            } else {
                courier.put(a.getCourier_service(), 1);
                //courier_index.put(a.getCourier_service(),ind);
                if (bool) {
                    price_list.add(courier_price);
                    bool = false;
                } else {
                    price_list.add(multi_courier_price);
                }
            }
        }
        return price_list;
    }
}
