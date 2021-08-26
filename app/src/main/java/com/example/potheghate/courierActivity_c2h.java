package com.example.potheghate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.potheghate.Adapter.c2hFromAdapter;
import com.example.potheghate.Adapter.c2hToAdapter;
import com.example.potheghate.Model.UserProfile;
import com.example.potheghate.Model.c_data_from;
import com.example.potheghate.Model.c_data_to;
import com.example.potheghate.Model.c_price;
import com.example.potheghate.Model.parcel;
import com.example.potheghate.utils.priceUtilsc2h;
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

public class courierActivity_c2h extends AppCompatActivity {
    public static final String TAG = "courier";
    private final HashMap<String, ArrayList<c_price>> price_all = new HashMap<>();
    private final HashMap<String, ArrayList<c_price>> price_all_courier = new HashMap<>();
    private final ArrayList<String> data = new ArrayList<>();
    private final HashMap<String, ArrayList<c_data_from>> data_from = new HashMap<>();
    private final HashMap<String, ArrayList<c_data_to>> data_to = new HashMap<>();
    private c2hFromAdapter adapterFrom;
    private c2hToAdapter adapterTo;
    ArrayList<String> dataArrayFrom = new ArrayList<>();
    ArrayList<String> dataArrayTo = new ArrayList<>();
    progressBar progBar;
    priceUtilsc2h price_util;
    private Button h2cButton, c2hButton;
    private UserProfile userThis;
    private boolean fromMultiple = true;
    private boolean toMultiple = false;
    private parcel p;
    private CoordinatorLayout rootView;
    private setEditTextOutsideClickableUI SEO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_c2h);
        progBar = new progressBar(this);
        progBar.show();
        Toolbar t = findViewById(R.id.p_toolbar);
        setSupportActionBar(t);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //TODO----------------responding to keyboard show hide events--------------------------------------
        rootView = findViewById(R.id.cordRootViewc2h);
        SEO=new setEditTextOutsideClickableUI(this, rootView,true);
        SEO.setupUI(rootView);
        checkKeyboard(rootView);
        //TODO----------------responding to keyboard show hide events--------------------------------------
        //----------radio------------------
        RadioGroup from = findViewById(R.id.fromGroup);
        RadioGroup to = findViewById(R.id.toGroup);

        h2cButton = findViewById(R.id.home2Courier);
        c2hButton = findViewById(R.id.courier2Home);

        h2cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),courierActivity_h2c.class));
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });
        Button fromPlus = findViewById(R.id.fromPlus);
        Button toPlus = findViewById(R.id.toPlus);
        fromPlus.setVisibility(View.GONE);
        toPlus.setVisibility(View.GONE);
        checkinfo();
        //--------------------adapters-------------------------------------
        // TODO----------------FromAdapter inside checkinfo function--------------------------------------
        //--------------------------------------------------------------
        setFromAdapter(fromMultiple);
        //--------------------------------------------------------------
        fromPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataArrayFrom.add("from");
                adapterFrom.notifyItemInserted(dataArrayFrom.size() - 1);

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
                RadioButton rq = group.findViewById(R.id.fromGroup_1);
                RadioButton rr = group.findViewById(R.id.fromGroup_2);
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
                if (validate && !checkCondition()) {
                    addValues();
                } else if (!validate) {
                   // Log.d(TAG, "onClick: 2");
                    progBar.dismiss();
                }
                //------------------validate here----------------------
            }
        });
    }

    private void multipleCheckedTask() {
        toMultiple = true;
        dataArrayTo.clear();
        setToAdapter(toMultiple);
        fromMultiple = false;
        dataArrayFrom.clear();
        setFromAdapter(fromMultiple);

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
        adapterFrom = new c2hFromAdapter(dataArrayFrom, fromSingle);
        RecyclerView r = findViewById(R.id.courierRecycleView);
        r.setAdapter(adapterFrom);
        SEO.setupUI(rootView);
    }

    private void setToAdapter(boolean toSingle) {
        dataArrayTo.add("to");
        adapterTo = new c2hToAdapter(dataArrayTo, toSingle,p);
        RecyclerView rt = findViewById(R.id.courierRecycleViewTo);
        rt.setAdapter(adapterTo);
        SEO.setupUI(rootView);
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
                    direct.putExtra("val", "courier_c2h");
                    direct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(direct);
                    finish();

                } else if (snapshot.exists()) {
                    importPrice();
                    userThis = snapshot.getValue(UserProfile.class);
                    p = new parcel(userThis.getname(), userThis.getaddress(), user.getPhoneNumber());
                    setToAdapter(toMultiple);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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
        for (String k : adapterFrom.view_rDetails.keySet()) {
            if (!toMultiple) {
                if (adapterFrom.view_details.get(k).getText().toString().trim().isEmpty()) {
                    adapterFrom.view_details.get(k).setError("insert Product details");
                    adapterFrom.view_details.get(k).requestFocus();
                    return false;
                }
                if (adapterFrom.view_quantity.get(k).getText().toString().trim().isEmpty()) {
                    adapterFrom.view_quantity.get(k).setError("insert Quantity");
                    adapterFrom.view_quantity.get(k).requestFocus();
                    return false;
                }
                if(adapterFrom.condition.get(k) && adapterFrom.view_cond_amnt.get(k).getText().toString().trim().isEmpty()){
                    adapterFrom.view_cond_amnt.get(k).setError("insert condition amount");
                    adapterFrom.view_cond_amnt.get(k).requestFocus();
                    return false;
                }
            }
            if (adapterFrom.view_rDetails.get(k).getText().toString().trim().isEmpty()) {
                adapterFrom.view_rDetails.get(k).setError("insert receiver details");
                adapterFrom.view_rDetails.get(k).requestFocus();
                return false;
            } else {
                flag1 = true;
            }


        }
        for (String k : adapterTo.view_name.keySet()) {
            if (toMultiple) {
                if (adapterTo.view_details.get(k).getText().toString().trim().isEmpty()) {
                    adapterTo.view_details.get(k).setError("insert Product details");
                    adapterTo.view_details.get(k).requestFocus();
                    return false;
                }
                if (adapterTo.view_quantity.get(k).getText().toString().trim().isEmpty()) {
                    adapterTo.view_quantity.get(k).setError("insert Quantity");
                    adapterTo.view_quantity.get(k).requestFocus();
                    return false;
                }
                if(adapterTo.condition.get(k) && adapterTo.view_cond_amnt.get(k).getText().toString().trim().isEmpty()){
                    adapterTo.view_cond_amnt.get(k).setError("insert condition amount");
                    adapterTo.view_cond_amnt.get(k).requestFocus();
                    return false;
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
                flag = true;
            }

        }
        return flag && flag1;
    }
    private void addValues() {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("courier_c2h");
        String userid = Objects.requireNonNull(user.getUid());
        String key = Objects.requireNonNull(ref.child(userid).push().getKey());

        //-----------------------check condition------------------------------
        for (String k : adapterFrom.spinnerValC.keySet()) {
            String service = adapterFrom.spinnerValC.get(k);
            String pckg = adapterFrom.spinnerValP.get(k).split("\\(")[0];

            ref.child(userid).child(key).child("from/" + k).child("courier_service").setValue(service);
            ref.child(userid).child(key).child("from/" + k).child("courier_package").setValue(pckg);
            ref.child(userid).child(key).child("from/" + k).child("receiver_details").setValue(adapterFrom.view_rDetails.get(k).getText().toString().trim());
        }
        for (String k : adapterTo.view_name.keySet()) {
            parcel dataFrom = new parcel(adapterTo.view_name.get(k).getText().toString().trim(),
                    adapterTo.view_address.get(k).getText().toString().trim(),
                    adapterTo.view_phone.get(k).getText().toString().trim());
            ref.child(userid).child(key).child("to/" + k).setValue(dataFrom);

        }

        if (!toMultiple) {
            for (String k : adapterFrom.view_rDetails.keySet()) {
                String cond;
                if (Objects.requireNonNull(adapterFrom.condition.get(k))) {
                    cond = "Yes";
                    ref.child(userid).child(key).child("from/" + k).child("condition_amount").setValue(Float.parseFloat(adapterFrom.view_cond_amnt.get(k).getText().toString().trim()));
                } else {
                    cond = "No";
                }
                String weight = adapterFrom.spinnerValW.get(k).split("\\(")[0];
                ref.child(userid).child(key).child("from/" + k).child("product_weight").setValue(weight);
                ref.child(userid).child(key).child("from/" + k).child("product_details").setValue(adapterFrom.view_details.get(k).getText().toString().trim());
                ref.child(userid).child(key).child("from/" + k).child("product_quantity").setValue(adapterFrom.view_quantity.get(k).getText().toString().trim());
                ref.child(userid).child(key).child("from/" + k).child("condition").setValue(cond);

            }
        } else {
            String de = "to/";
            if (adapterTo.view_name.size() < 2) {
                de = "from/";
            }
            //when singleTo but product in toSection then migrate data from toSection to fromSection in DB
            for (String k : adapterTo.view_name.keySet()) {
                String cond;
                if (Objects.requireNonNull(adapterTo.condition.get(k))) {
                    cond = "Yes";
                    ref.child(userid).child(key).child(de + k).child("condition_amount").setValue(Float.parseFloat(adapterFrom.view_cond_amnt.get(k).getText().toString().trim()));
                } else {
                    cond = "No";
                }
                String weight = adapterTo.spinnerValW.get(k).split("\\(")[0];
                ref.child(userid).child(key).child(de + k).child("product_weight").setValue(weight);
                ref.child(userid).child(key).child(de + k).child("product_details").setValue(adapterTo.view_details.get(k).getText().toString().trim());
                ref.child(userid).child(key).child(de + k).child("product_quantity").setValue(adapterTo.view_quantity.get(k).getText().toString().trim());
                ref.child(userid).child(key).child(de + k).child("condition").setValue(cond);

            }
        }

        //TODO--------------read to verify successful data upload and set price in database--------------
        setPriceDatabase();

    }

    private void setPriceDatabase() {
        price_util = new priceUtilsc2h(price_all, price_all_courier);
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("courier_c2h").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String orderId = snap.getKey();
                        data.add(orderId);
                        ArrayList<c_data_from> data_f = new ArrayList<>();
                        ArrayList<c_data_to> data_t = new ArrayList<>();
                        for (DataSnapshot sn : snap.child("from").getChildren()) {
                            c_data_to a = sn.getValue(c_data_to.class);
                            data_t.add(a);
                        }
                        data_to.put(orderId, data_t);
                        for (DataSnapshot sn : snap.child("to").getChildren()) {
                            c_data_from a = sn.getValue(c_data_from.class);
                            data_f.add(a);
                        }
                        data_from.put(orderId, data_f);

                    }
                    //Log.d(TAG, "onDataChange: " +data.size());
                    for (String a : data) {
                       ref.child("courier_c2h").child(user.getUid()).child(a).child("price").setValue(price_util.calcPrice( data_from.get(a),data_to.get(a)));
                    }

                    Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                    clear_data();
                    startActivity(new Intent(getApplicationContext(), courierActivity_c2h_checkout.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private boolean checkCondition() {
        //-----------------------check condition------------------------------
        boolean b = false;
        for (String k : adapterFrom.view_rDetails.keySet()) {
            if (adapterFrom.condition.get(k) != null && adapterFrom.condition.get(k)
                    && Float.parseFloat(adapterFrom.view_cond_amnt.get(k).getText().toString().trim())>3000.0f) {
                b = true;
                break;
            }
        }
        for (String k : adapterTo.view_name.keySet()) {
            if (adapterTo.condition.get(k) != null && adapterTo.condition.get(k)
                    && Float.parseFloat(adapterTo.view_cond_amnt.get(k).getText().toString().trim())>3000.0f) {
                b = true;
                break;
            }
        }

        if (b) {
            checkImage();
        }
        Log.d(TAG, "checkCondition ");
        return b;
    }

    private void checkImage() {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("images/" + user.getUid());
        listRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                dialogMpass();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogM();
                progBar.dismiss();
            }
        });

    }

    private void dialogM() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(getApplicationContext(), docSubmitActivity.class));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
    private void dialogMpass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addValues();
            }
        });
        builder.setMessage(R.string.dialog_message_pass).setTitle(R.string.dialog_title);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
    private void clear_data() {
        data.clear();
        data_from.clear();
        data_to.clear();
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
                    Log.d(TAG, "onDataChange: " + price_all_courier.size());
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
        else if(item.getItemId() == R.id.courierMenu){
            startActivity(new Intent(getApplicationContext(),courierActivity_c2h_checkout.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void hideSoftkeybard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        findViewById(R.id.bottomButtonPanel).setVisibility(View.VISIBLE);
    }
    private void checkKeyboard(CoordinatorLayout c) {
        c.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                c.getWindowVisibleDisplayFrame(r);
                int screenHeight = c.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    findViewById(R.id.bottomButtonPanel).setVisibility(View.GONE);
                } else {
                    //findViewById(R.id.bottomButtonPanel).setVisibility(View.VISIBLE);
                }
            }
        });
    }
}