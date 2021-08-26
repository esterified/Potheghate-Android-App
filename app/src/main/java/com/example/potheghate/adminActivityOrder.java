package com.example.potheghate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.potheghate.Adapter.adminOrderAdapter;
import com.example.potheghate.Model.UserProfile;
import com.example.potheghate.Model.admin_o_data;
import com.example.potheghate.Model.c_data_from;
import com.example.potheghate.Model.c_data_to;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class adminActivityOrder extends AppCompatActivity {
    private static final String TAG = "admin";
    private adminOrderAdapter mAdapter;
    private RecyclerView re;

    private final ArrayList<String> data_main = new ArrayList<>();
    private final ArrayList<admin_o_data> data_order = new ArrayList<>();
    private final HashMap<String,UserProfile> data_user = new HashMap();
    private final ArrayList<String> data_branch = new ArrayList<>();
    private final HashMap<String, ArrayList<c_data_from>> data_from = new HashMap<>();
    private final HashMap<String, ArrayList<c_data_to>> data_to = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);
        Toolbar t = findViewById(R.id.p_toolbar);
        setSupportActionBar(t);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        addData();
    }
    private void addData() {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                 for(DataSnapshot da:snapshot.getChildren()){
                     Log.d(TAG, "onDataChange: "+da.getKey());
                     data_order.clear();
                     for(DataSnapshot sn:da.getChildren()){
                         data_order.add(sn.getValue(admin_o_data.class));
                     }

                     data_main.add(da.getKey());
                 }
                addUserData();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
    private void addUserData() {
        //FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(String d:data_main){
                        data_user.put(d,snapshot.child(d).getValue(UserProfile.class));
                    }

                    mAdapter=new adminOrderAdapter(data_main,data_user, data_order);
                    re=findViewById(R.id.recyler_o_parent);
                    re.setAdapter(mAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
    private void addDatam() {

        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("courier_h2c").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String orderId = snap.getKey();
                        data_branch.add(orderId);
                        ArrayList<c_data_from> data_f = new ArrayList<>();
                        ArrayList<c_data_to> data_t = new ArrayList<>();
                        for (DataSnapshot sn : snap.child("from").getChildren()) {
                            c_data_from a = sn.getValue(c_data_from.class);
                            data_f.add(a);
                        }
                        data_from.put(orderId, data_f);
                        for (DataSnapshot sn : snap.child("to").getChildren()) {
                            c_data_to a = sn.getValue(c_data_to.class);
                            data_t.add(a);
                        }
                        data_to.put(orderId, data_t);
                        //price_list.add(snap.child("price").getValue(Float.class));
                        //calcPrice(data_f,data_t);
                    }
                    //Log.d(TAG, "onDataChange: " +data.size());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


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