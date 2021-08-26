package com.example.potheghate.Adapter;

import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Model.Cart;
import com.example.potheghate.R;
import com.example.potheghate.dashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.viewHolder> {
    public static final String TAG="cart";
    private final ArrayList<Cart> cartdata;
    public cartAdapter(ArrayList<Cart> cartdata) {
        this.cartdata = cartdata;
    }

    @NonNull
    @Override
    public cartAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreate");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list,parent, false);
        return new viewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull cartAdapter.viewHolder holder, int position) {
        Log.d(TAG,"onBind");
        //holder.getCardView().setAnimation(AnimationUtils.loadAnimation(holder.getNameView().getContext(), R.anim.fadeandslide));
        Cart data=cartdata.get(position);
       // data.setTotalprice();
        if(data.getImage()!=null){
            Picasso.get().load(Uri.parse(data.getImage())).fit().centerCrop().into(holder.getImageView());
        }

        holder.getNameView().setText(data.getName());

        //holder.getCardView().setForeground(ContextCompat.getDrawable(holder.getCardView().getContext(),R.drawable.foreground_mark));
        holder.getPriceView().setText(dashboardActivity.currency(data.getTotalprice()));
        //holder.getVariantView().setText(data.getVariant_id());
        holder.getQuantityView().setText(data.getQuantity());
        EditText editText=holder.getQuantityView();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(TAG,"beforetextchanged");
                //if(s!=null){updateQuantity(position,editText.getText().toString(),data);}
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d(TAG,"ontextchanged");
                //if(s!=null){updateQuantity(position,editText.getText().toString(),data);}
            }
            @Override
            public void afterTextChanged(Editable s) {
               // Log.d(TAG,"aftertextchanged");
                if(!s.toString().isEmpty()){
                  //  Log.d(TAG,"not empty");
                    updateQuantity(position,editText.getText().toString(),cartdata.get(position));
                }
            }
        });
        holder.getButtonPlus().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int val = Integer.parseInt(editText.getText().toString());
                    val++;
                    editText.setText(String.valueOf(val));
                }
                catch (NumberFormatException ignored){
                }

            }
        });
        holder.getButtonMinus().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int val = Integer.parseInt(editText.getText().toString());
                    if (val>0){
                        val--;
                        editText.setText(String.valueOf(val));
                    }

                }
                catch (NumberFormatException ignored){

                }

            }
        });
        holder.getButtonDel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cartdata.remove(cartdata.get(position));
                //notifyItemRemoved(position);
                removeItem(position,data.getId(),cartdata);

            }
        });
    }
    private void updateQuantity(int position,String quantity,Cart data){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dataref =FirebaseDatabase.getInstance().getReference().child("cart/"+user.getUid()+"/"+ data.getId()+"/quantity");
        DatabaseReference dataref1 =FirebaseDatabase.getInstance().getReference().child("cart/"+user.getUid()+"/"+ data.getId()+"/totalprice");
        dataref.setValue(quantity).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                data.setQuantity(quantity);
                if(!quantity.equals("0")){
                    data.calcTotalprice();
                    dataref1.setValue(data.getTotalprice());///no listener required
                }
                notifyItemChanged(position);
            }
        });


    }
    private void removeItem(int position,String cartid,ArrayList<Cart> cartdata){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String quantity="0";
        DatabaseReference dataref =FirebaseDatabase.getInstance().getReference().child("cart/"+user.getUid()+"/"+cartid+"/quantity");

        dataref.setValue(quantity).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                    //cartdata.remove(cartdata.get(position));
                    //notifyItemRemoved(position);


            }
        });


    }

    @Override
    public int getItemCount() {
        return cartdata.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private final ImageView imageView;
        private final TextView nameView,priceView,variantView;
        private final EditText quantityView;
        private final Button buttonDel,buttonPlus,buttonMinus;

        public CardView getCardView() {
            return cardView;
        }

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewList1V);
            nameView = itemView.findViewById(R.id.listNameV);
            priceView = itemView.findViewById(R.id.listPriceV);
            variantView= itemView.findViewById(R.id.listvariant);
            quantityView=itemView.findViewById(R.id.inputQuantity);
            buttonDel=itemView.findViewById(R.id.list_clear_button);
            buttonMinus=itemView.findViewById(R.id.buttonListVremove);
            buttonPlus=itemView.findViewById(R.id.buttonListVadd);
            cardView=itemView.findViewById(R.id.cardRoot1V);

        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getNameView() {
            return nameView;
        }

        public TextView getPriceView() {
            return priceView;
        }

        public TextView getVariantView() {
            return variantView;
        }

        public EditText getQuantityView() {
            return quantityView;
        }

        public Button getButtonDel() {
            return buttonDel;
        }

        public Button getButtonPlus() {
            return buttonPlus;
        }

        public Button getButtonMinus() {
            return buttonMinus;
        }



    }
}
