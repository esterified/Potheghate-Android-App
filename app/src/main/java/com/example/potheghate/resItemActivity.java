package com.example.potheghate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Adapter.customAdapterItm;
import com.example.potheghate.Model.R_ItemData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.potheghate.dashboardActivity.getCount;

public class resItemActivity extends AppCompatActivity {
    customAdapterItm adapter;
    RecyclerView _rR;
    ShimmerFrameLayout shimmerLayout;
    String resId;
    ArrayList<R_ItemData> Data_ = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_item);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //----------------------toolbar------------------
        Toolbar nav_tool = findViewById(R.id.p_toolbar1);
        setSupportActionBar(nav_tool);
        //intent value;
        //Toast.makeText(getApplicationContext(),getIntent().getStringExtra("name"),Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //----------------------toolbar------------------
        //----------------------shimmer------------------
        shimmerLayout = findViewById(R.id.shimmerFlayout1);
        _rR = findViewById(R.id.RecycleView1);
        //----------------------shimmer------------------
        resId = getIntent().getStringExtra("name");
        //----------------------IMAGE download------------------

        FirebaseAuth firebaseAuth;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //StorageReference storageReference = firebaseStorage.getReference();
        addHeaderImage();
        addUri(databaseReference);
        //----------------------IMAGE download------------------
    }

    private void addUri(DatabaseReference dataref) {

        Query query = dataref.child("restaurant/" + resId + "/item").orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        //int index=Integer.parseInt(snap.getKey());
                        String id = snap.getKey();
                        boolean flipflop=false;
                        if(snap.child("subcategory").exists()) {
                           flipflop= Objects.requireNonNull(snap.child("subcategory").getValue(boolean.class));


                        }
                        String a = snap.child("name").getValue(String.class);
                        //String b="100";
                        Integer b = snap.child("price").getValue(Integer.class);

                        String c = snap.child("image").getValue(String.class);

                        Data_.add(new R_ItemData(a, b, c, id, flipflop));

                    }
                    adapter = new customAdapterItm(Data_, resId);
                    _rR.setAdapter(adapter);
                    shimmerLayout.setVisibility(View.GONE);
                    _rR.setVisibility(View.VISIBLE);
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
   private void addHeaderImage(){

       ImageView collapseImageView=findViewById(R.id.CollapseImage);
       TextView textView=findViewById(R.id.resTitleText);
       String image=getIntent().getStringExtra("image");
       String text=getIntent().getStringExtra("title");
       textView.setText(text);
       Uri uri=Uri.parse(image);
       Picasso.get().load(uri).fit().centerCrop().into(collapseImageView);


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

        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}