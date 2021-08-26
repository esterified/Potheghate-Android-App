package com.example.potheghate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Model.c_price;
import com.example.potheghate.Model.h2h_data_from;
import com.example.potheghate.R;

import java.util.ArrayList;
import java.util.HashMap;

public class h2hCheckoutAdapter extends RecyclerView.Adapter<h2hCheckoutAdapter.viewHolder> {
    public static final String TAG = "checkout";
    private final ArrayList<String> data;
    private final HashMap<String, ArrayList<h2h_data_from>> data_from;
    private final HashMap<String, ArrayList<h2h_data_from>> data_to;
    private final ArrayList<Float> price_list;
    private h2hCheckoutAdapter.OnRemoveListener listener;
    private HashMap<String, ArrayList<c_price>> price_all ;


    public h2hCheckoutAdapter(ArrayList<String> data, HashMap<String, ArrayList<h2h_data_from>> data_from, HashMap<String,
            ArrayList<h2h_data_from>> data_to, ArrayList<Float> price_list,HashMap<String, ArrayList<c_price>> price_all) {
        this.data = data;
        this.data_from = data_from;
        this.data_to = data_to;
        this.price_list = price_list;
        this.price_all=price_all;
    }

    public void SetOnRemoveListener(h2hCheckoutAdapter.OnRemoveListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public h2hCheckoutAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courier_h2c_checkout_list, parent, false);
        return new h2hCheckoutAdapter.viewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull h2hCheckoutAdapter.viewHolder holder, int position) {
        holder.getOrderId().setText(maskString(data.get(position)));
        //Log.d(TAG, "onBindViewHolder: H");
        String order_price = "Tk." + price_list.get(position);
        holder.getViewPrice().setText(order_price);
        h2hChildFromAdapter adapter = new h2hChildFromAdapter(data_from.get(data.get(position)),price_all);
        holder.getViewFromR().setAdapter(adapter);
        h2hChildToAdapter adapterT = new h2hChildToAdapter(data_to.get(data.get(position)),price_all,data_from.get(data.get(position)));
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


        public viewHolder(@NonNull View itemView, h2hCheckoutAdapter.OnRemoveListener listener) {
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
