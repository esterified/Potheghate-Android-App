package com.example.potheghate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Model.c_price;
import com.example.potheghate.Model.h2h_data_from;
import com.example.potheghate.R;
import com.example.potheghate.utils.priceUtilsh2h;
import java.util.ArrayList;
import java.util.HashMap;

public class h2hChildFromAdapter extends RecyclerView.Adapter<h2hChildFromAdapter.viewHolder> {
    private final ArrayList<h2h_data_from> data_from;
    private final HashMap<String, ArrayList<c_price>> price_all ;
    private final String Title="From";


    public h2hChildFromAdapter(ArrayList<h2h_data_from> data_from,HashMap<String, ArrayList<c_price>> price_all) {
        this.data_from = data_from;
        this.price_all=price_all;
    }

    @NonNull
    @Override
    public h2hChildFromAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.h2h_list_layout_from,parent,false);
        return new h2hChildFromAdapter.viewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull h2hChildFromAdapter.viewHolder holder, int position) {
        holder.getViewTitle().setText(Title);
        h2h_data_from data=data_from.get(position);
        String weight_price="";
        String person_price="";
        priceUtilsh2h pru=new priceUtilsh2h(price_all);
        if(position>0){
            person_price="(+Tk."+ price_all.get("multi_person_h2h").get(0).getPrice()+")";
        }
        holder.getViewName().setText( new String("Name: ").concat(data.getName()).concat(person_price));
        holder.getViewAddress().setText(new String("Address: ").concat(data.getAddress()));
        holder.getViewPhone().setText(new String("Phone: ").concat(data.getPhone()));
        if(data.getProduct_weight()!=null){
            weight_price="(+Tk."+pru.calcMaterialPrice(data.getProduct_weight())+")";
            holder.getViewDetails().setText(new String("Product: ").concat(data.getProduct_details()));
            holder.getViewWeight().setText(new String("Weight: ").concat(data.getProduct_weight()).concat(weight_price));
            holder.getViewCond().setText(new String("Condition:").concat(data.getCondition()));
            if(data.getCondition().equals("Yes")){
                holder.getViewCAmount().setText(new String("Condition Amount: Tk.").concat(String.valueOf(data.getProduct_condition_amount())));
                holder.getViewCPayer().setText(new String("Condition Payer:").concat(data.getProduct_condition_payer()));
            }
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
        private final TextView viewCond;
        private final TextView viewTitle;
        private final TextView viewCAmount;
        private final TextView viewCPayer;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            this.viewName=itemView.findViewById(R.id.fll_name);
            this.viewPhone=itemView.findViewById(R.id.fll_phone);
            this.viewAddress=itemView.findViewById(R.id.fll_address);
            this.viewDetails=itemView.findViewById(R.id.fll_p_details);
            this.viewWeight=itemView.findViewById(R.id.fll_p_weight);
            this.viewCond=itemView.findViewById(R.id.fll_p_cond);
            this.viewTitle=itemView.findViewById(R.id.fll_title);
            this.viewCAmount=itemView.findViewById(R.id.fll_p_cond_amount);
            this.viewCPayer=itemView.findViewById(R.id.fll_p_cond_payer);
        }

        public TextView getViewCPayer() {
            return viewCPayer;
        }

        public TextView getViewCAmount() {
            return viewCAmount;
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

        public TextView getViewTitle() {
            return viewTitle;
        }
    }
}

