package com.example.potheghate.Interface;

import com.example.potheghate.Model.V_Data;
////interface to import the recycler variant form values into the main activity
public interface variantInterface {
    public void putDataRadio(String string,V_Data data);
    public void putDataCheckbox(String string,V_Data data);
    public void removeDataCheckbox(String string,V_Data data);
}
