package com.example.potheghate.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.potheghate.R;
import com.example.potheghate.docSubmitActivity;

public class progressBar {
    private final AlertDialog dialog;
    public progressBar(Activity a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        // set the custom layout
        final View customLayout = a.getLayoutInflater().inflate(R.layout.custom_progress_bar, null);
        builder.setView(customLayout);
        this.dialog = builder.create();
        this.dialog.setCancelable(false);


    }
    public void show(){
        this.dialog.show();
        this.dialog.getWindow().setLayout(100,100);
    }
    public void dismiss(){
        this.dialog.dismiss();
    }
}
