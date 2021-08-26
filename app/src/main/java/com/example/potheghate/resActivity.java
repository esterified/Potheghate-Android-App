package com.example.potheghate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Adapter.customAdapter;
import com.example.potheghate.Model.R_Data;
import com.example.potheghate.Model.V_Data;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.example.potheghate.dashboardActivity.getCount;
import static java.lang.Integer.parseInt;

public class resActivity extends AppCompatActivity {
    private static final int MAX_COUNT = 10;
    private EditText searchView;
    private Button searchCancel;
    NestedScrollView nested;
    customAdapter adapter;
    RecyclerView _rR;
    ShimmerFrameLayout shimmerLayout;
    ArrayList<R_Data> Data_ = new ArrayList<>();
    ArrayList<R_Data> tempData_= new ArrayList<>();
    private ArrayList<R_Data> List=new ArrayList<>();
    ProgressBar bar;
    public static String TAG="fayed";
    private String lastkey = "1";
    private boolean notEnd = true;
    private boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //----------------------toolbar------------------
        Toolbar nav_tool = findViewById(R.id.p_toolbar);
        setSupportActionBar(nav_tool);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //----------------------toolbar------------------
        //----------------------shimmer------------------

        nested = findViewById(R.id.NestedScroll);
        shimmerLayout = findViewById(R.id.shimmerFlayout);
        _rR = findViewById(R.id.RecycleView);
        bar = findViewById(R.id.onLoadBar);
        //----------------------shimmer------------------
        //_rR.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // _rR.setHasFixedSize(true);
        searchView=findViewById(R.id.searchBarRes);
        searchCancel=findViewById(R.id.searchCancel);

        //----------------------IMAGE download------------------
        FirebaseAuth firebaseAuth;
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("restaurant").orderByKey().limitToFirst(MAX_COUNT);
        //StorageReference storageReference = firebaseStorage.getReference();
        //scroll parameter to manipulate the function for different purposes
        addUri(query,0);
        //----------------------Pagination------------------
        nested.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int x = _rR.getBottom()-100;
                int y = v.getScrollY() + v.getHeight();
               // Log.d(TAG,y+"::"+x);
                if (y > x) {
                    if (notEnd && flag) {
                        flag=false;
                        bar.setVisibility(View.VISIBLE);
                        Query query = databaseReference.child("restaurant").orderByKey().startAfter(lastkey).limitToFirst(MAX_COUNT);
                        addUri(query,1);
                    }
                }
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    searchCancel.setVisibility(View.VISIBLE);
                    DatabaseReference dataref = FirebaseDatabase.getInstance().getReference();
                    Query query = dataref.child("restaurant").orderByKey();
                    doQuery(query,s.toString().toLowerCase());
                   // adapter.getFilter().filter(s);
                }
            // Toast.makeText(getApplicationContext(),s.toString(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCancel.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(),"ss",Toast.LENGTH_SHORT).show();
               Log.d(TAG, List.size()+"");
               searchView.setText("");
               Data_.clear();
               Data_.addAll(tempData_);
               adapter.notifyDataSetChanged();
            }
        });

    }

    private void doQuery(Query query,String text) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    List.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String a = snap.child("name").getValue(String.class);
                        String b = snap.child("location").getValue(String.class);
                        String id = snap.getKey();
                        String uri = snap.child("image").getValue(String.class);
                        String open = snap.child("open_at").getValue(String.class);
                        String close = snap.child("close_at").getValue(String.class);

                        long itemcount = snap.child("item").getChildrenCount();
                        boolean internal=false;
                        for(DataSnapshot sn:snap.child("item").getChildren()){
                            String x=sn.child("name").getValue(String.class);
                            if(x!=null && x.toLowerCase().contains(text)){
                                internal=true;
                            }
                        }
                        if((a!=null && a.toLowerCase().contains(text) )|| (b!=null && b.toLowerCase().contains(text)) ){

                            List.add(new R_Data(a, b, id,open,close, itemcount, uri));

                        }
                        else if(internal){
                            List.add(new R_Data(a, b, id,open,close, itemcount, uri));
                        }

                    }
                    Data_.clear();
                    Data_.addAll(List);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addUri(Query query,int scroll) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        //int index=Integer.parseInt(snap.getKey());
                        if (snapshot.getChildrenCount() < MAX_COUNT) {
                            notEnd = false;//that means it is the ENd :D
                        }
                        long itemcount=snap.child("item").getChildrenCount();
                        String id = snap.getKey();
                        String a = snap.child("name").getValue(String.class);
                        String b = snap.child("location").getValue(String.class);
                        String uri = snap.child("image").getValue(String.class);
                        String open = snap.child("open_at").getValue(String.class);
                        String close = snap.child("close_at").getValue(String.class);
                        Data_.add(new R_Data(a, b, id,open,close, itemcount, uri));
                        count++;
                        if (count == MAX_COUNT) {lastkey = snap.getKey(); }
                    }
                    if(scroll==0) {
                        tempData_.addAll(Data_);
                        adapter = new customAdapter(getApplicationContext(), Data_);
                        _rR.setAdapter(adapter);
                        shimmerLayout.setVisibility(View.GONE);
                        bar.setVisibility(View.GONE);
                        _rR.setVisibility(View.VISIBLE);
                    }
                    else{
                        tempData_.addAll(Data_);
                        //Log.d(TAG,Data_.size()-count+"::"+count);
                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemRangeInserted(Data_.size()-count,count);
                        bar.setVisibility(View.GONE);
                        _rR.setVisibility(View.VISIBLE);
                        flag=true;
                    }
                 query.removeEventListener(this);
                } else {
                    shimmerLayout.setVisibility(View.GONE);
                    Snackbar.make(_rR, "Sorry No Item Found", Snackbar.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
                //Snackbar.make(v, "Cart " + textview.getText(), Snackbar.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),cartActivity.class));
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cartMenu) {// User chose the "Settings" item, show the app settings UI...
            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
        }
        else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}