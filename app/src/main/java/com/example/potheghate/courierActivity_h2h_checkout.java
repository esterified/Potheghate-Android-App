package com.example.potheghate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Adapter.h2hCheckoutAdapter;
import com.example.potheghate.Model.c_price;
import com.example.potheghate.Model.h2h_data_from;
import com.example.potheghate.utils.orderNotify;
import com.example.potheghate.utils.progressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class courierActivity_h2h_checkout extends AppCompatActivity {
    public static final String TAG = "checkout";
    public static final String PAYMENT_METHOD = "payment_method";
    public static final String OWNER = "owner";
    public static final String TYPE = "type";
    public static final String PRICE = "price";
    public static final String TIME = "time";
    public static final String BULK = "bulk";
    private final ArrayList<String> data = new ArrayList<>();
    private final HashMap<String, ArrayList<h2h_data_from>> data_from = new HashMap<>();
    private final HashMap<String, ArrayList<h2h_data_from>> data_to = new HashMap<>();
    private final HashMap<String, ArrayList<c_price>> price_all = new HashMap<>();
    private final ArrayList<Float> price_list = new ArrayList<>();
    private static final String ORDER = "order";
    private TextView totalPrice;
    private h2hCheckoutAdapter adapterMain;
    private progressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_h2h_checkout);
        progBar = new progressBar(this);
        progBar.show();
        Toolbar t = findViewById(R.id.p_toolbar);
        setSupportActionBar(t);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //---------------------------------------------------------------
        totalPrice = findViewById(R.id.checkout_total_price);
        importPrice();
        Button submit = findViewById(R.id.place_order_B);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progBar.show();
                submitData();
            }
        });
    }


    private void submitData() {

        RadioGroup rGroup = findViewById(R.id.G_c);
        RadioButton rB = findViewById(rGroup.getCheckedRadioButtonId());
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("courier_h2h").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String key = ref.child(ORDER).push().getKey();
                    ref.child(ORDER).child(user.getUid()).child(key).child(TIME).setValue(getCurrentTime());
                    ref.child(ORDER).child(user.getUid()).child(key).child(PRICE).setValue(getTotalPrice(price_list));
                    ref.child(ORDER).child(user.getUid()).child(key).child(TYPE).setValue("h2h");
                    // ref.child(ORDER).child(key).child(OWNER).setValue(user.getUid());
                    ref.child(ORDER).child(user.getUid()).child(key).child(PAYMENT_METHOD).setValue(rB.getText().toString());
                    for (DataSnapshot sn : snapshot.getChildren()) {
                        ref.child(ORDER).child(user.getUid()).child(key).child(BULK).child(sn.getKey()).setValue(sn.getValue());
                    }
                    Toast.makeText(getApplicationContext(), "Order Successful", Toast.LENGTH_SHORT).show();
                    orderNotify n = new orderNotify(getApplicationContext());
                    n.sendFCMMessage();
                    //-----------clear h2h cart values-----------------
                    ref.child("courier_h2h").child(user.getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(getApplicationContext(), orderTrackingActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private float getTotalPrice(ArrayList<Float> price_list) {
        float pr = 0.0f;
        for (float p : price_list) {
            pr += p;
        }
        return pr;
    }

    private void setTotalPrice(Float price) {
        String text = "Total Price: Tk." + price;
        totalPrice.setText(text);
    }

    private void importPrice() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("courier");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot sn : snapshot.getChildren()) {
                        ArrayList<c_price> ar = new ArrayList<>();
                        for (DataSnapshot s : sn.getChildren()) {
                            ar.add(s.getValue(c_price.class));
                        }
                        price_all.put(sn.getKey(), ar);
                    }
                    addData();
                } else {
                    Toast.makeText(getApplicationContext(), "PriceFetchError", Toast.LENGTH_SHORT).show();
                }
                progBar.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addData() {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("courier_h2h").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String orderId = snap.getKey();
                        data.add(orderId);
                        ArrayList<h2h_data_from> data_f = new ArrayList<>();
                        ArrayList<h2h_data_from> data_t = new ArrayList<>();
                        for (DataSnapshot sn : snap.child("from").getChildren()) {
                            data_f.add(sn.getValue(h2h_data_from.class));
                            //Log.d(TAG, "onDataChange: "+sn.getValue(h2h_data_from.class).getProduct_weight());
                        }
                        data_from.put(orderId, data_f);
                        for (DataSnapshot s : snap.child("to").getChildren()) {
                            data_t.add(s.getValue(h2h_data_from.class));

                        }
                        data_to.put(orderId, data_t);
                        price_list.add(snap.child("price").getValue(Float.class));
                        //calcPrice(data_f,data_t);
                    }
                    //Log.d(TAG, "onDataChange: " +data.size());
                    adapterMain = new h2hCheckoutAdapter(data, data_from, data_to, price_list, price_all);
                    RecyclerView r = findViewById(R.id.checkoutRecycleView);
                    r.setAdapter(adapterMain);
                    setTotalPrice(getTotalPrice(price_list));
                    adapterMain.SetOnRemoveListener(new h2hCheckoutAdapter.OnRemoveListener() {
                        @Override
                        public void onRemove(int position) {
                            remove(data.get(position));
                            data.remove(position);
                            price_list.remove(position);
                            setTotalPrice(getTotalPrice(price_list));
                            Log.d(TAG, "onRemove: ");
                            adapterMain.notifyItemRemoved(position);
                            adapterMain.notifyItemRangeChanged(position, data.size() - position);
                        }
                    });
                }
                setDatalistener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void setDatalistener() {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("courier_h2h").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    LinearLayout L = findViewById(R.id.place_order);
                    L.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout L = findViewById(R.id.place_order);
                    L.setVisibility(View.GONE);
                    //TODO end activity when the list is empty
                }
                progBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goback();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goback();
    }

    private void goback() {
        startActivity(new Intent(getApplicationContext(), courierActivity_h2h.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void remove(String key) {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("courier_h2h");
        ref.child(user.getUid()).child(key).setValue(null);
    }

    private String getCurrentTime() {
        Locale currentLocale = new Locale.Builder().setRegion("BD").build();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", currentLocale);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+06:00"));
        return sdf.format(new Date());
    }
}