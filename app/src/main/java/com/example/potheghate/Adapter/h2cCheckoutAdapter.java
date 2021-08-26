package com.example.potheghate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Model.c_data_from;
import com.example.potheghate.Model.c_data_to;
import com.example.potheghate.Model.c_price;
import com.example.potheghate.R;

import java.util.ArrayList;
import java.util.HashMap;


public class h2cCheckoutAdapter extends RecyclerView.Adapter<h2cCheckoutAdapter.viewHolder> {
    public static final String TAG = "checkout";
    private final ArrayList<String> data;
    private final HashMap<String, ArrayList<c_data_from>> data_from;
    private final HashMap<String, ArrayList<c_data_to>> data_to;
    private final ArrayList<Float> price_list;
    private OnRemoveListener listener;
    private HashMap<String, ArrayList<c_price>> price_all ;
    private HashMap<String, ArrayList<c_price>> price_all_courier ;

    public h2cCheckoutAdapter(ArrayList<String> data, HashMap<String, ArrayList<c_data_from>> data_from, HashMap<String,
            ArrayList<c_data_to>> data_to, ArrayList<Float> price_list,HashMap<String, ArrayList<c_price>> price_all,HashMap<String, ArrayList<c_price>> price_all_courier) {
        this.data = data;
        this.data_from = data_from;
        this.data_to = data_to;
        this.price_list = price_list;
        this.price_all=price_all;
        this.price_all_courier=price_all_courier;
    }

    public void SetOnRemoveListener(OnRemoveListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courier_h2c_checkout_list, parent, false);
        return new viewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull h2cCheckoutAdapter.viewHolder holder, int position) {
        holder.getOrderId().setText(maskString(data.get(position)));
        //Log.d(TAG, "onBindViewHolder: H");
        String order_price = "Tk." + price_list.get(position);
        holder.getViewPrice().setText(order_price);
        h2cChildFromAdapter adapter = new h2cChildFromAdapter(data_from.get(data.get(position)),price_all,price_all_courier);
        holder.getViewFromR().setAdapter(adapter);
        h2cChildToAdapter adapterT = new h2cChildToAdapter(data_to.get(data.get(position)),price_all,price_all_courier,data_from.get(data.get(position)));
        holder.getViewToR().setAdapter(adapterT);
    }

    private String maskString(String s) {
        return "***** " + s.substring(s.length() - 4, s.length() - 1);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnRemoveListener {
        public void onRemove(int position);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView orderId;
        private final RecyclerView ViewFromR;
        private final RecyclerView ViewToR;
        private final Button clearButton;
        private final TextView ViewPrice;


        public viewHolder(@NonNull View itemView, OnRemoveListener listener) {
            super(itemView);

            this.orderId = itemView.findViewById(R.id.C_orderId);
            this.ViewFromR = itemView.findViewById(R.id.C_From_RecycleView);
            this.ViewToR = itemView.findViewById(R.id.C_To_RecycleView);
            this.clearButton = itemView.findViewById(R.id.C_clear_button);
            this.ViewPrice = itemView.findViewById(R.id.C_price);
            this.clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        //Log.d(TAG, "onClick: " + getAdapterPosition());
                        listener.onRemove(getAdapterPosition());

                    }

                }
            });


        }

        public TextView getViewPrice() {
            return ViewPrice;
        }

        public Button getClearButton() {
            return clearButton;
        }

        public RecyclerView getViewFromR() {
            return ViewFromR;
        }

        public RecyclerView getViewToR() {
            return ViewToR;
        }

        public TextView getOrderId() {
            return orderId;
        }
    }
}
