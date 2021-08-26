package com.example.potheghate;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Adapter.variantAdapter;
import com.example.potheghate.Interface.variantInterface;
import com.example.potheghate.Model.Cart;
import com.example.potheghate.Model.R_ItemData;
import com.example.potheghate.Model.V_Data;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.potheghate.Adapter.variantAdapter.CHECKBOX;
import static com.example.potheghate.dashboardActivity.getCount;

public class variantActivity extends AppCompatActivity implements variantInterface {

    public static final String TAG = "trans";
    public static final String CHANNEL_ID = "fcm_channel_id";
    variantAdapter adapter;
    ArrayList<String> keyset = new ArrayList<>();
    ArrayList<ArrayList<V_Data>> datalist = new ArrayList<>();
    ArrayList<String> layoutTypeList = new ArrayList<>();
    public ArrayList<V_Data> vDataArrayList=new ArrayList<>();
    public Map<String,V_Data> vDatavariant =new HashMap<>();
    Button addButton;
    EditText quantityView;
    RecyclerView _rR;
    String resId, itemId;
    private R_ItemData itemData;
    private final int totalPrice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //createNotificationChannel();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variant);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //----------------------toolbar------------------
        Toolbar nav_tool = findViewById(R.id.p_toolbar1V);
        setSupportActionBar(nav_tool);
        //intent value;
        //Toast.makeText(getApplicationContext(),getIntent().getStringExtra("name"),Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //----------------------toolbar------------------

        //----------------------shimmer------------------
        _rR = findViewById(R.id.RecycleView1V_variant);
        //_rR_A = findViewById(R.id.RecycleView1V_addon);

        addButton = findViewById(R.id.addButton);
        quantityView = findViewById(R.id.inputVar);
        //----------------------intent extra values------------------
        resId = getIntent().getStringExtra("resId");
        itemId = getIntent().getStringExtra("itemId");

        //----------------------IMAGE download------------------
        FirebaseAuth firebaseAuth;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        addButton.setEnabled(false);

        ///////////////////////////////////
        getItemData(databaseReference);
        addUri(databaseReference, variantAdapter.RADIO, _rR);
        addUri(databaseReference, CHECKBOX, _rR);
        //----------------------IMAGE download------------------
        // Log.d(TAG,radio.getCheckedRadioButtonId()+" ");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart cart = new Cart();
                cart.setName(itemData.getName());
                cart.setQuantity(quantityView.getText().toString());
                cart.setItem_id(itemData.getId());
                cart.setSubcategory(itemData.isSubcategory());
                cart.setImage(itemData.getImage());
                cart.setRes_id(resId);
                int price = totalPrice + calcPrice() + itemData.getPrice();
                cart.setPrice(price);
                addToCart(cart);
 ////////////////////////////debug purposes//////////////////////////////
/*                Log.d(TAG, variantAdapterChild.vDatavariant.get("flavour").getName() + "");
                Log.d(TAG, " " + variantAdapterChild.vDatavariant.get("variant").getName() + " ");

                for (V_Data a : variantAdapterChild.vDataArrayList) {
                    Log.d(TAG, "with " + a.getName() + "");
                }*/
////////////////////////////debug purposes//////////////////////////////
            }
        });
    }
    private int calcPrice() {
        int p = 0;
        for (String key : vDatavariant.keySet()) {
            int price = Objects.requireNonNull(vDatavariant.get(key)).getPrice();
            p += price;
        }
        for (V_Data a : vDataArrayList) {
            int price = a.getPrice();
            p += price;
        }
        return p;
    }

    private void addVariants(String keyNode, DatabaseReference ref) {

        DatabaseReference ref1 = ref.child(keyNode + "/subcatValue");
        for (String key : vDatavariant.keySet()) {
            ref1.push().setValue(vDatavariant.get(key));

        }
        for (V_Data a : vDataArrayList) {
            ref1.push().setValue(a);

        }
    }

    private void addToCart(Cart cart) {
        FirebaseUser a = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cart").child(a.getUid());
        String key = Objects.requireNonNull(ref.push().getKey());
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Log.d(TAG, "do Transaction");
                cart.calcTotalprice();
                mutableData.child(key).setValue(cart);
                // Set value and report transaction success
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                Log.d(TAG, "on Complete");
                addVariants(key, ref);
                // Transaction completed
                //Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void addUri(DatabaseReference dataref, String temp, RecyclerView _R) {

        Query query = dataref.child("subcategory/" + resId + itemId + "/" + temp).orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String name = snap.getKey();
                        ArrayList<V_Data> dataholder = new ArrayList<>();
                        for (DataSnapshot snap1 : snap.getChildren()) {
                            String variantId = snap1.getKey();
                            String a = snap1.child("name").getValue(String.class);
                            Integer b = snap1.child("price").getValue(Integer.class);
                            String c = snap1.child("image").getValue(String.class);
                            if (c != null) {
                                Uri uri = Uri.parse(c);
                                dataholder.add(new V_Data(a, b, name, temp, resId, itemId, variantId, uri));

                            } else {
                                dataholder.add(new V_Data(a, b, name, temp, resId, itemId, variantId));
                            }

                        }
                        datalist.add(dataholder);
                        keyset.add(name);
                        layoutTypeList.add(temp);

                    }
                    adapter = new variantAdapter(keyset, datalist, layoutTypeList,variantActivity.this);
                    _R.setAdapter(adapter);
                    _R.setVisibility(View.VISIBLE);
                    // Log.d(TAG,""+ datalist.size());


                } else {
                    //if(temp.equals("variant")){ addItem(dataref);}
                    Snackbar.make(_R, "Sorry No Item Found", Snackbar.LENGTH_LONG).show();
                    finish();
                }
                query.removeEventListener(this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getItemData(DatabaseReference ref) {
        Query query = ref.child("restaurant/" + resId + "/item/" + itemId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot sn) {
                if (sn.exists()) {
                    String name = sn.child("name").getValue(String.class);
                    Integer price = sn.child("price").getValue(Integer.class);
                    String image = sn.child("image").getValue(String.class);
                    String id = itemId;
                    boolean subcategory = true;///////always true for variants

                    itemData = new R_ItemData(name, price, image, id, subcategory);
                    ImageView collapseImageView=findViewById(R.id.CollapseImage);
                    TextView textView=findViewById(R.id.resTitleText);
                    textView.setText(name);
                    Uri uri=Uri.parse(image);
                    Picasso.get().load(uri).fit().centerCrop().into(collapseImageView);

                    addButton.setEnabled(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "this is a sample notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.cartMenu);
        item.setActionView(R.layout.cart_badge_action);

        TextView textview = item.getActionView().findViewById(R.id.actionbar_notifcation_textview);
        textview.setVisibility(View.INVISIBLE);
        getCount(textview);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), cartActivity.class));
                //Snackbar.make(v, "Cart " + textview.getText(), Snackbar.LENGTH_LONG).show();
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cartMenu) {// User chose the "Settings" item, show the app settings UI...
            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void putDataRadio(String string, V_Data data) {
        if(string!=null && !string.isEmpty()) {
            vDatavariant.put(string, data);
        }
    }

    @Override
    public void putDataCheckbox(String string, V_Data data) {
        vDataArrayList.add(data);
    }

    @Override
    public void removeDataCheckbox(String string, V_Data data) {
        vDataArrayList.remove(data);
    }
}