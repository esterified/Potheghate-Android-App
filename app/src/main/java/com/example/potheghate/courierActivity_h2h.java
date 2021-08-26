package com.example.potheghate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.potheghate.Adapter.h2hFromAdapter;
import com.example.potheghate.Model.UserProfile;
import com.example.potheghate.Model.c_data_from;
import com.example.potheghate.Model.c_data_to;
import com.example.potheghate.Model.c_price;
import com.example.potheghate.Model.h2h_data_from;
import com.example.potheghate.Model.parcel;
import com.example.potheghate.utils.priceUtilsh2c;
import com.example.potheghate.utils.priceUtilsh2h;
import com.example.potheghate.utils.progressBar;
import com.example.potheghate.utils.setEditTextOutsideClickableUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class courierActivity_h2h extends AppCompatActivity {
    public static final String TAG = "courier";
    private final HashMap<String, ArrayList<c_price>> price_all = new HashMap<>();
    private final ArrayList<String> data = new ArrayList<>();
    private final HashMap<String, ArrayList<h2h_data_from>> data_from = new HashMap<>();
    private final HashMap<String, ArrayList<h2h_data_from>> data_to = new HashMap<>();
    h2hFromAdapter adapterFrom;
    h2hFromAdapter adapterTo;
    ArrayList<String> dataArrayFrom = new ArrayList<>();
    ArrayList<String> dataArrayTo = new ArrayList<>();
    progressBar progBar;
    priceUtilsh2h price_util;
    private UserProfile userThis;
    private boolean fromMultiple = true;
    private boolean toMultiple = false;
    private parcel p;
    private CoordinatorLayout rootView;
    private setEditTextOutsideClickableUI SEO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_h2h);
        progBar = new progressBar(this);
        progBar.show();
        Toolbar t = findViewById(R.id.p_toolbar);
        setSupportActionBar(t);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rootView = findViewById(R.id.cordRootView);
        SEO=new setEditTextOutsideClickableUI(this, rootView,false);
        SEO.setupUI(rootView);

        //TODO----------------responding to keyboard show hide events--------------------------------------
        //----------radio------------------
        RadioGroup from = findViewById(R.id.fromGroup);
        RadioGroup to = findViewById(R.id.toGroup);
        Button fromPlus = findViewById(R.id.fromPlus);
        Button toPlus = findViewById(R.id.toPlus);
        fromPlus.setVisibility(View.GONE);
        toPlus.setVisibility(View.GONE);
        checkinfo();
        //--------------------adapters-------------------------------------
        // TODO----------------FromAdapter inside checkinfo function--------------------------------------
        //--------------------------------------------------------------
        setToAdapter(toMultiple);
        //--------------------------------------------------------------
        fromPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataArrayFrom.add("from");
                adapterFrom.notifyItemInserted(dataArrayFrom.size() - 1);
                adapterFrom.setPNull();
            }
        });
        toPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataArrayTo.add("to");
                adapterTo.notifyItemInserted(dataArrayTo.size() - 1);
                adapterTo.setPNull();
            }
        });
        from.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rr = group.findViewById(R.id.fromGroup_2);
                RadioButton rq = group.findViewById(R.id.fromGroup_1);
                if (rr.isChecked()) {
                    singleCheckedTask();
                    fromPlus.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onCheckedChanged: ");
                    if (to.getCheckedRadioButtonId() == R.id.toGroup_2) {
                        RadioButton r2 = to.findViewById(R.id.toGroup_2);
                        RadioButton r1 = to.findViewById(R.id.toGroup_1);
                        r2.setChecked(false);
                        r1.setChecked(true);
                    }
                } else if (rq.isChecked()) {
                    multipleCheckedTask();
                    fromPlus.setVisibility(View.GONE);
                }

            }
        });
        to.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rq = group.findViewById(R.id.toGroup_1);
                RadioButton rr = group.findViewById(R.id.toGroup_2);
                if (rr.isChecked()) {
                    multipleCheckedTask();
                    //----------------------------------
                    toPlus.setVisibility(View.VISIBLE);
                    //Log.d(TAG, rr.getText().toString());
                    if (from.getCheckedRadioButtonId() == R.id.fromGroup_2) {
                        RadioButton r2 = from.findViewById(R.id.fromGroup_2);
                        RadioButton r1 = from.findViewById(R.id.fromGroup_1);
                        r2.setChecked(false);
                        r1.setChecked(true);
                    }
                } else if (rq.isChecked()) {
                    singleCheckedTask();
                    //---------------------------------
                    toPlus.setVisibility(View.GONE);
                }
            }
        });
        //TODO---------------checkout button------------------------
        Button placeOrder = findViewById(R.id.placeOrder);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validate=validate();
                progBar.show();
                hideSoftkeybard(v);
                if (validate) {
                   addValues();
                } else {
                    //Log.d(TAG, "onClick: ");
                    progBar.dismiss();
                }
                //------------------validate here----------------------
            }
        });
    }
    private void multipleCheckedTask() {
        fromMultiple = false;
        dataArrayFrom.clear();
        setFromAdapter(fromMultiple);
        toMultiple = true;
        dataArrayTo.clear();
        setToAdapter(toMultiple);
    }

    private void singleCheckedTask() {
        toMultiple = false;
        dataArrayTo.clear();
        setToAdapter(toMultiple);
        fromMultiple = true;
        dataArrayFrom.clear();
        setFromAdapter(fromMultiple);
    }

    private void setFromAdapter(boolean fromSingle) {
        dataArrayFrom.add("from");
        adapterFrom = new h2hFromAdapter(dataArrayFrom, fromSingle, p);
        RecyclerView r = findViewById(R.id.courierRecycleView);
        r.setAdapter(adapterFrom);
        SEO.setupUI(rootView);
    }

    private void setToAdapter(boolean toSingle) {
        dataArrayTo.add("to");
        adapterTo = new h2hFromAdapter(dataArrayTo, toSingle, p);
        adapterTo.setPNull();
        RecyclerView rt = findViewById(R.id.courierRecycleViewTo);
        rt.setAdapter(adapterTo);
        SEO.setupUI(rootView);
    }

    public boolean p_error(String s){
        Pattern p = Pattern.compile("(^[0][0-9]{10}$)");
        Matcher m = p.matcher(s);
/*        if(m.find()){
            Log.d(TAG, "p_error: "+m.group());
        }*/
        return (m.find() && m.group().equals(s));
    }
    private boolean validate() {
        boolean flag = false;
        boolean flag1 = false;
        for (String k : adapterFrom.view_name.keySet()) {
            if (!toMultiple) {
                if (adapterFrom.view_details.get(k).getText().toString().trim().isEmpty()) {
                    adapterFrom.view_details.get(k).setError("insert Product details");
                    adapterFrom.view_details.get(k).requestFocus();
                    return false;
                }
                if (adapterFrom.condition.get(k)) {
                    if(adapterFrom.condition_amount.get(k).getText().toString().trim().isEmpty()){
                        adapterFrom.condition_amount.get(k).setError("insert amount");
                        adapterFrom.condition_amount.get(k).requestFocus();
                    }

                }
            }
            if (adapterFrom.view_name.get(k).getText().toString().trim().isEmpty()) {
                adapterFrom.view_name.get(k).setError("insert name");
                adapterFrom.view_name.get(k).requestFocus();
                return false;
            } else if (adapterFrom.view_address.get(k).getText().toString().trim().isEmpty()) {
                adapterFrom.view_address.get(k).setError("insert address");
                adapterFrom.view_address.get(k).requestFocus();
                return false;
            } else if (adapterFrom.view_phone.get(k).getText().toString().trim().isEmpty() || adapterFrom.view_phone.get(k).getText().toString().length() < 11) {
                adapterFrom.view_phone.get(k).setError("insert a valid phone number");
                adapterFrom.view_phone.get(k).requestFocus();
                return false;
            } else {
                flag = true;
            }
        }
        for (String k : adapterTo.view_name.keySet()) {
            if (toMultiple) {
                if (adapterTo.view_details.get(k).getText().toString().trim().isEmpty()) {
                    adapterTo.view_details.get(k).setError("insert aProduct details");
                    adapterTo.view_details.get(k).requestFocus();
                    return false;
                }
                if (adapterTo.condition.get(k)) {
                    if(adapterTo.condition_amount.get(k).getText().toString().trim().isEmpty()){
                        adapterTo.condition_amount.get(k).setError("insert amount");
                        adapterTo.condition_amount.get(k).requestFocus();
                    }

                }
            }
            if (adapterTo.view_name.get(k).getText().toString().trim().isEmpty()) {
                adapterTo.view_name.get(k).setError("insert name");
                adapterTo.view_name.get(k).requestFocus();
                return false;
            } else if (adapterTo.view_address.get(k).getText().toString().trim().isEmpty()) {
                adapterTo.view_address.get(k).setError("insert address");
                adapterTo.view_address.get(k).requestFocus();
                return false;
            } else if (adapterTo.view_phone.get(k).getText().toString().trim().isEmpty() || !p_error(adapterTo.view_phone.get(k).getText().toString().trim())) {
                adapterTo.view_phone.get(k).setError("insert a valid phone number");
                adapterTo.view_phone.get(k).requestFocus();
                return false;
            } else {
                flag1 = true;
            }
        }

        return flag && flag1;
    }
    private void addValues() {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("courier_h2h");
        String userid = Objects.requireNonNull(user.getUid());
        String key = Objects.requireNonNull(ref.child(userid).push().getKey());

        //-----------------------check condition------------------------------
        for (String k : adapterFrom.view_name.keySet()) {
            parcel dataFrom = new parcel(adapterFrom.view_name.get(k).getText().toString().trim(),
                    adapterFrom.view_address.get(k).getText().toString().trim(),
                    adapterFrom.view_phone.get(k).getText().toString().trim());
            ref.child(userid).child(key).child("from/" + k).setValue(dataFrom);

        }
        for (String k : adapterTo.view_name.keySet()) {
            parcel dataTo = new parcel(adapterTo.view_name.get(k).getText().toString().trim(),
                    adapterTo.view_address.get(k).getText().toString().trim(),
                    adapterTo.view_phone.get(k).getText().toString().trim());
            ref.child(userid).child(key).child("to/" + k).setValue(dataTo);

        }
        if (!toMultiple) {
            for (String k : adapterFrom.view_name.keySet()) {
                String cond;
                if (Objects.requireNonNull(adapterFrom.condition.get(k))) {
                    cond = "Yes";
                    Float amount=Float.parseFloat(adapterFrom.condition_amount.get(k).getText().toString().trim());
                    ref.child(userid).child(key).child("from/" + k).child("product_condition_amount").setValue(amount);
                    ref.child(userid).child(key).child("from/" + k).child("product_condition_payer").setValue(adapterFrom.condition_payer.get(k));
                } else {
                    cond = "No";
                }
                String weight = adapterFrom.spinnerValW.get(k).split("\\(")[0];
                ref.child(userid).child(key).child("from/" + k).child("product_weight").setValue(weight);
                ref.child(userid).child(key).child("from/" + k).child("product_details").setValue(adapterFrom.view_details.get(k).getText().toString().trim());
                ref.child(userid).child(key).child("from/" + k).child("condition").setValue(cond);

            }
        } else {
            String de = "to/";
            if (adapterTo.view_name.size() == 1) {
                de = "from/";
            }
            //when singleTo but product in toSection then migrate data from toSection to fromSection in DB
            for (String k : adapterTo.view_name.keySet()) {
                String cond;
                if (Objects.requireNonNull(adapterTo.condition.get(k))) {
                    cond = "Yes";
                    Float amount=Float.parseFloat(adapterFrom.condition_amount.get(k).getText().toString().trim());
                    ref.child(userid).child(key).child(de + k).child("product_condition_amount").setValue(amount);
                    ref.child(userid).child(key).child(de + k).child("product_condition_payer").setValue(adapterFrom.condition_payer.get(k));
                } else {
                    cond = "No";
                }
                String weight = adapterTo.spinnerValW.get(k).split("\\(")[0];
                ref.child(userid).child(key).child(de + k).child("product_weight").setValue(weight);
                ref.child(userid).child(key).child(de + k).child("product_details").setValue(adapterTo.view_details.get(k).getText().toString().trim());
                ref.child(userid).child(key).child(de + k).child("condition").setValue(cond);

            }
        }

        //TODO--------------read to verify successful data upload and set price in database--------------
        setPriceDatabase();

    }
    private void setPriceDatabase() {
        price_util = new priceUtilsh2h(price_all);
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
                        }
                        data_from.put(orderId, data_f);
                        for (DataSnapshot sn : snap.child("to").getChildren()) {
                            data_t.add(sn.getValue(h2h_data_from.class));

                        }
                        data_to.put(orderId, data_t);
                    }
                    //Log.d(TAG, "onDataChange: " +data.size());
                    for (String a : data) {
                        //update all order price database
                        ref.child("courier_h2h").child(user.getUid()).child(a).child("price").setValue(price_util.calcPrice(data_from.get(a), data_to.get(a)));
                    }

                    Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                    clear_data();
                    startActivity(new Intent(getApplicationContext(), courierActivity_h2h_checkout.class));
                }
                // progBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    private void clear_data() {
        data.clear();
        data_from.clear();
        data_to.clear();
    }

    private void checkinfo() {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        // Read from the database
        myRef.child("users/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("name").exists() || !snapshot.child("email").exists() || !snapshot.child("address").exists()) {
                    Intent direct = new Intent(getApplicationContext(), profileActivity.class);
                    direct.putExtra("val", "courier_h2h");
                    direct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(direct);
                    finish();

                } else if (snapshot.exists()) {
                    importPrice();
                    userThis = snapshot.getValue(UserProfile.class);
                    p = new parcel(userThis.getname(), userThis.getaddress(), user.getPhoneNumber());
                    setFromAdapter(fromMultiple);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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

    private void hideSoftkeybard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.courier_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else if (item.getItemId() == R.id.courierMenu) {
            startActivity(new Intent(getApplicationContext(),courierActivity_h2h_checkout.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



}