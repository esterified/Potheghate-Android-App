package com.example.potheghate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.potheghate.Model.Cart;
import com.example.potheghate.Adapter.cartAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

public class cartActivity extends AppCompatActivity {
    private final ArrayList<Cart> cartdata=new ArrayList<>();
    private RecyclerView recyclerView;
    private cartAdapter adapter;
    public static final String TAG="cart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button clearallButton = findViewById(R.id.clear_button);
        ///////////////////////////////////////////
        Button floatButton = findViewById(R.id.floatingButton);
        dashboardActivity.getCount(floatButton);
        ///////////////////////////////////////////////
        recyclerView=findViewById(R.id.RecycleView_cart);
        FirebaseUser user= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference dataref= FirebaseDatabase.getInstance().getReference("cart/"+user.getUid());
        getValues(dataref);
        cancelButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();

        }
    });
        clearallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(cartdata);

            }
        });


    }

    private void getValues(DatabaseReference dataref) {

     Query query=dataref.orderByKey();
     query.addValueEventListener(new ValueEventListener() {

         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {

             if(snapshot.exists()){
                 if(cartdata.size()>0){
                     cartdata.clear();
                     adapter.notifyDataSetChanged();

                 }
                 for(DataSnapshot snap:snapshot.getChildren()){
                     String id=snap.getKey();
                     String zero="0";
                     String name=snap.child("name").getValue(String.class);
                     String res_id=snap.child("res_id").getValue(String.class);
                     String item_id=snap.child("item_id").getValue(String.class);
                     boolean subcategory=Objects.requireNonNull(snap.child("subcategory").getValue(boolean.class));
                     String quantity=snap.child("quantity").getValue(String.class);
                     assert quantity != null;
                     Integer price=snap.child("price").getValue(Integer.class);
                     String image=snap.child("image").getValue(String.class);
                     if(subcategory){
                         String List=" (";
                         for(DataSnapshot sn:snap.child("subcatValue").getChildren()){
                             String t=sn.child("name").getValue(String.class);
                             List=List.concat(t.concat(", "));
                         }
                         List=List.concat(" )");
                         name=name.concat(List);
                     }
                     if(!quantity.equals(zero))
                     {
                         cartdata.add(new Cart( id, name,  res_id, item_id, subcategory, quantity, price,image));
                     }

                 }
                 Log.d(TAG,cartdata.size()+"");
                 if(cartdata.size()<1){
                     cartisEmpty();

                 }
                 else{
                     adapter=new cartAdapter(cartdata);
                     recyclerView.setAdapter(adapter);
                     recyclerView.setVisibility(View.VISIBLE);
                     cartisNotEmpty();

                 }

                 //---------------removing the listener--------------------------
                 //query.removeEventListener(this);
             }
             else{
                 cartisEmpty();
             }

         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });

    }
    private void cartisEmpty(){
        Button button=findViewById(R.id.checkoutbutton);
        button.setVisibility(View.GONE);
    }
    private void cartisNotEmpty(){
        Button button=findViewById(R.id.checkoutbutton);
        button.setVisibility(View.VISIBLE);
    }

    private void removeItem(ArrayList<Cart> cartdata){
        ///remove all items
        FirebaseUser user= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        for(Cart cart:cartdata){

            DatabaseReference dataref =FirebaseDatabase.getInstance().getReference().child("cart/"+user.getUid()+"/"+cart.getId());
            dataref.child("quantity").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                       // cartdata.remove(cart);
                      //  adapter.notifyItemRemoved(cartdata.indexOf(cart));
                }
            });
        }




    }
}