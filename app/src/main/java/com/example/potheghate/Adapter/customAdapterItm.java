package com.example.potheghate.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Model.Cart;
import com.example.potheghate.Model.R_ItemData;
import com.example.potheghate.R;
import com.example.potheghate.dashboardActivity;
import com.example.potheghate.variantActivity;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;


public class customAdapterItm extends RecyclerView.Adapter<customAdapterItm.ViewHolder> {
    private static final String TAG = "add to cart";
    public final String resId;
    private final ArrayList<R_ItemData> localdata;
    FirebaseUser User;

    public customAdapterItm(ArrayList<R_ItemData> localdata, String id) {
        this.localdata = localdata;
        this.resId = id;
        this.User = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.res_item_list, parent, false);

        return new ViewHolder(view,localdata,resId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ///--------------animation--------------------
        CardView cardView = viewHolder.getCardView();
        cardView.setAnimation(AnimationUtils.loadAnimation(viewHolder.getCardView().getContext(), R.anim.fadeandslide));
        ////-------------Shimmer initializer-----------------
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setDropoff(50)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        //////////////////////////////////////
        R_ItemData _shopData = localdata.get(position);
        viewHolder.getPriceView().setText(dashboardActivity.currency(_shopData.getPrice()));
        viewHolder.getNameView().setText(_shopData.getName());
        Picasso.get().load(Uri.parse(_shopData.getImage())).fit().centerCrop().into(viewHolder.getImageView());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_shopData.isSubcategory()) {
                    Cart cart = new Cart();
                    cart.setName(_shopData.getName());
                    cart.setQuantity("1");
                    cart.setItem_id(_shopData.getId());
                    cart.setSubcategory(_shopData.isSubcategory());
                    cart.setImage(_shopData.getImage());
                    cart.setRes_id(resId);
                    cart.setPrice(_shopData.getPrice());
                    addToCart(cart);
                    //viewHolder.getImageView().setForeground(ContextCompat.getDrawable(viewHolder.getImageView().getContext(), R.drawable.foreground_mark));
                } else {
                    Intent a = new Intent(v.getContext(), variantActivity.class);
                    a.putExtra("resId", resId);
                    a.putExtra("itemId", _shopData.getId());

                    // Toast.makeText(mContext,id+" "+_shopData.getId()+"",Toast.LENGTH_LONG ).show();
                    v.getContext().startActivity(a);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return localdata.size();
    }

    private void addToCart(Cart cart) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cart").child(User.getUid());

        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                //Log.d(TAG, cart.getItem_id());
                //Log.d(TAG, "do Transaction");
                String key = "";
                for (MutableData data : mutableData.getChildren()) {
                    Cart cart1 = Objects.requireNonNull(data.getValue(Cart.class));

                    if (cart.getRes_id().equals(cart1.getRes_id()) && cart.isSubcategory() == cart1.isSubcategory()
                            && cart.getItem_id().equals(cart1.getItem_id())) {
                        Integer quantity1 = Integer.parseInt(cart1.getQuantity());
                        Integer quantity2 = Integer.parseInt(cart.getQuantity());
                        cart.setQuantity(String.valueOf(quantity1 + quantity2));
                        key = data.getKey();
                        break;
                    }
                }
                if (key.isEmpty()) {
                    key = ref.push().getKey();
                }
                cart.calcTotalprice();
                mutableData.child(key).setValue(cart);
                // Set value and report transaction success
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot currentData) {
                Log.d(TAG, "on Complete");
                // Transaction completed
                //Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView priceView;
        private final TextView nameView;
        private final ImageView ImageView;
        private final CardView CardView;
        private final TextView badgeView;

        public ViewHolder(@NonNull View itemView,ArrayList<R_ItemData> localdata,String resId) {
            super(itemView);
            priceView = itemView.findViewById(R.id.listPrice);
            nameView = itemView.findViewById(R.id.listName);
            ImageView = itemView.findViewById(R.id.imageViewList1);
            CardView = itemView.findViewById(R.id.cardRoot1);
            badgeView = itemView.findViewById(R.id.cart_badge);
            checkQuantity(localdata,resId);
        }

        private void checkQuantity(ArrayList<R_ItemData> localdata,String resId){
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cart").child(user.getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                            R_ItemData dat=localdata.get(getAdapterPosition());
                            for(DataSnapshot sn:snapshot.getChildren()){
                                Cart cart = Objects.requireNonNull(sn.getValue(Cart.class));
                                if(cart.getRes_id().equals(resId) && cart.isSubcategory() == dat.isSubcategory() && cart.getItem_id().equals(dat.getId())){
                                    int quantity=Integer.parseInt(cart.getQuantity());
                                    if(quantity>0){
                                        badgeView.setText(String.valueOf(quantity));
                                        badgeView.setVisibility(View.VISIBLE);
                                    }
                                    else if(quantity<=0){
                                        badgeView.setVisibility(View.GONE);
                                    }

                                }
                            }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public TextView getPriceView() {
            return priceView;
        }

        public TextView getNameView() {
            return nameView;
        }

        public android.widget.ImageView getImageView() {
            return ImageView;
        }

        public androidx.cardview.widget.CardView getCardView() {
            return CardView;
        }
    }

}
