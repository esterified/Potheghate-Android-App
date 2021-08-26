package com.example.potheghate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Objects;

public class adminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        Toolbar t = findViewById(R.id.p_toolbar);
        setSupportActionBar(t);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    public void onClickOrder(View view) {
        startActivity(new Intent(getApplicationContext(),adminActivityOrder.class));
    }

    public void onClickRole(View view) {
        startActivity(new Intent(getApplicationContext(),adminActivityRole.class));
    }

    public void onClickWallet(View view) {
        startActivity(new Intent(getApplicationContext(),adminActivityWallet.class));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.courier_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}