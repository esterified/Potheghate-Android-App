package com.example.potheghate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.media.session.MediaSessionCompat;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.potheghate.utils.orderNotify;
import com.google.firebase.database.util.GAuthToken;
import com.google.firebase.messaging.RemoteMessage;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Adapter.h2cCheckoutAdapter;
import com.example.potheghate.Model.c_data_from;
import com.example.potheghate.Model.c_data_to;
import com.example.potheghate.Model.c_price;
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
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class courierActivity_h2c_checkout extends AppCompatActivity {
    public static final String CHANNEL_ID = "fcm_channel_id";
    public static final String TAG = "checkout";
    public static final String PAYMENT_METHOD = "payment_method";
    public static final String OWNER = "owner";
    public static final String TYPE = "type";
    public static final String PRICE = "price";
    public static final String TIME = "time";
    public static final String BULK = "bulk";
    private final ArrayList<String> data = new ArrayList<>();
    private final HashMap<String, ArrayList<c_data_from>> data_from = new HashMap<>();
    private final HashMap<String, ArrayList<c_data_to>> data_to = new HashMap<>();
    private final HashMap<String, ArrayList<c_price>> price_all = new HashMap<>();
    private final HashMap<String, ArrayList<c_price>> price_all_courier = new HashMap<>();
    private final ArrayList<Float> price_list = new ArrayList<>();
    private static final String ORDER="order";
    private TextView totalPrice;
    private h2cCheckoutAdapter adapterMain;
    private progressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_h2c_checkout);
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
        ref.child("courier_h2c").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String key = ref.child(ORDER).push().getKey();
                    ref.child(ORDER).child(user.getUid()).child(key).child(TIME).setValue(getCurrentTime());
                    ref.child(ORDER).child(user.getUid()).child(key).child(PRICE).setValue(getTotalPrice(price_list));
                    ref.child(ORDER).child(user.getUid()).child(key).child(TYPE).setValue("h2c");
                    //ref.child(ORDER).child(key).child(OWNER).setValue(user.getUid());
                    ref.child(ORDER).child(user.getUid()).child(key).child(PAYMENT_METHOD).setValue(rB.getText().toString());
                    for (DataSnapshot sn : snapshot.getChildren()) {
                        ref.child(ORDER).child(user.getUid()).child(key).child(BULK).child(sn.getKey()).setValue(sn.getValue());
                    }
                    Toast.makeText(getApplicationContext(), "Order Successful", Toast.LENGTH_SHORT).show();
                    orderNotify n=new orderNotify(getApplicationContext());
                    n.sendFCMMessage();
                    //-----------clear h2c cart values-----------------
                    ref.child("courier_h2c").child(user.getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(getApplicationContext(), orderTrackingActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
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
                    for (DataSnapshot sn : snapshot.child("courier_service").getChildren()) {
                        ArrayList<c_price> ar = new ArrayList<>();
                        for (DataSnapshot d : sn.child("package").getChildren()) {
                            ar.add(new c_price(d.getKey(), d.getValue(Float.class)));
                        }
                        price_all_courier.put(sn.child("name").getValue(String.class), ar);
                    }
                    addData();
                    //Log.d(TAG, "onDataChange: " + price_all_courier.size());
                } else {
                    Toast.makeText(getApplicationContext(), "PriceFetchError", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addData() {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("courier_h2c").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String orderId = snap.getKey();
                        data.add(orderId);
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
                        price_list.add(snap.child("price").getValue(Float.class));
                        //calcPrice(data_f,data_t);
                    }
                    //Log.d(TAG, "onDataChange: " +data.size());
                    adapterMain = new h2cCheckoutAdapter(data, data_from, data_to, price_list,price_all,price_all_courier);
                    RecyclerView r = findViewById(R.id.checkoutRecycleView);
                    r.setAdapter(adapterMain);
                    setTotalPrice(getTotalPrice(price_list));
                    adapterMain.SetOnRemoveListener(new h2cCheckoutAdapter.OnRemoveListener() {
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
        ref.child("courier_h2c").child(user.getUid()).addValueEventListener(new ValueEventListener() {
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

    @Override
    public void onBackPressed() {
        goback();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
          goback();
        }
        return super.onOptionsItemSelected(item);
    }

private void goback(){
    startActivity(new Intent(getApplicationContext(), courierActivity_h2c.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    finish();
}
    private void remove(String key) {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("courier_h2c");
        ref.child(user.getUid()).child(key).setValue(null);
    }
    private String getCurrentTime(){
        Locale currentLocale = new Locale.Builder().setRegion("BD").build();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", currentLocale);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+06:00"));
        return sdf.format(new Date());
    }



}