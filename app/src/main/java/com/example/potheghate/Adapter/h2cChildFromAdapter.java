package com.example.potheghate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Model.c_data_from;
import com.example.potheghate.Model.c_data_to;
import com.example.potheghate.Model.c_price;
import com.example.potheghate.R;
import com.example.potheghate.utils.priceUtilsh2c;

import java.util.ArrayList;
import java.util.HashMap;

public class h2cChildFromAdapter extends RecyclerView.Adapter<h2cChildFromAdapter.viewHolder> {
    private final ArrayList<c_data_from> data_from;
    private final HashMap<String, ArrayList<c_price>> price_all ;
    private final HashMap<String, ArrayList<c_price>> price_all_courier ;


    public h2cChildFromAdapter(ArrayList<c_data_from> data_from,HashMap<String, ArrayList<c_price>> price_all,HashMap<String, ArrayList<c_price>> price_all_courier) {
        this.data_from = data_from;
        this.price_all=price_all;
        this.price_all_courier=price_all_courier;

    }

    @NonNull
    @Override
    public h2cChildFromAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.h2c_list_layout_from,parent,false);
        return new h2cChildFromAdapter.viewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull h2cChildFromAdapter.viewHolder holder, int position) {
        c_data_from data=data_from.get(position);
        String weight_price="";
        String quantity_price="";
        String person_price="";
        String condition_amount_value="";
        priceUtilsh2c pru=new priceUtilsh2c(price_all,price_all_courier);

        if(position>0){
            person_price="(+Tk."+ price_all.get("multi_person").get(0).getPrice()+")";
        }

        holder.getViewName().setText( new String("Name: ").concat(data.getName()).concat(person_price));
        holder.getViewAddress().setText(new String("Address: ").concat(data.getAddress()));
        holder.getViewPhone().setText(new String("Phone: ").concat(data.getPhone()));
        if(data.getProduct_weight()!=null){
            weight_price="(+Tk."+pru.calcMaterialPrice(data.getProduct_weight())+")";
            holder.getViewDetails().setText(new String("Product: ").concat(data.getProduct_details()));
            holder.getViewWeight().setText(new String("Weight: ").concat(data.getProduct_weight()).concat(weight_price));
            holder.getViewQuantity().setText(new String("Quantity: ").concat(data.getProduct_quantity()).concat(quantity_price));

            if(data.getCondition().equals("Yes")){
                condition_amount_value="("+data.getCondtion_amount()+") ";
            }
            holder.getViewCond().setText(new String("Condition:").concat(data.getCondition()).concat(condition_amount_value));
        }

    }

    @Override
    public int getItemCount() {
        return data_from.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView viewName;
        private final TextView viewPhone;
        private final TextView viewAddress;
        private final TextView viewDetails;
        private final TextView viewWeight;
        private final TextView viewQuantity;
        private final TextView viewCond;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            this.viewName=itemView.findViewById(R.id.fll_name);
            this.viewPhone=itemView.findViewById(R.id.fll_phone);
            this.viewQuantity=itemView.findViewById(R.id.fll_p_quantity);
            this.viewAddress=itemView.findViewById(R.id.fll_address);
            this.viewDetails=itemView.findViewById(R.id.fll_p_details);
            this.viewWeight=itemView.findViewById(R.id.fll_p_weight);
            this.viewCond=itemView.findViewById(R.id.fll_p_cond);
        }

        public TextView getViewCond() {
            return viewCond;
        }

        public TextView getViewName() {
            return viewName;
        }

        public TextView getViewPhone() {
            return viewPhone;
        }

        public TextView getViewAddress() {
            return viewAddress;
        }

        public TextView getViewDetails() {
            return viewDetails;
        }

        public TextView getViewWeight() {
            return viewWeight;
        }

        public TextView getViewQuantity() {
            return viewQuantity;
        }
    }
}
