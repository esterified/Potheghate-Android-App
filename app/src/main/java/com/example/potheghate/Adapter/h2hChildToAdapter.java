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

public class h2hChildToAdapter extends RecyclerView.Adapter<h2hChildToAdapter.viewHolder> {
    private final ArrayList<h2h_data_from> data_from;
    private final ArrayList<h2h_data_from> data_to;
    private final HashMap<String, ArrayList<c_price>> price_all ;
    private final String Title="To";
    private boolean weightFlag=false;

    public h2hChildToAdapter(ArrayList<h2h_data_from> data_to, HashMap<String, ArrayList<c_price>> price_all, ArrayList<h2h_data_from> data_from) {
        this.data_from = data_from;
        this.data_to=data_to;
        this.price_all=price_all;
        weightLoop();

    }
    private void weightLoop(){
        for(h2h_data_from t:data_from){
            if(t.getProduct_weight()!=null && !t.getProduct_weight().equals("5kg or less")){
                weightFlag=true;
                break;
            }
        }
        for(h2h_data_from a:data_to){
            if(a.getProduct_weight()!=null && !a.getProduct_weight().equals("5kg or less")){
                weightFlag=true;
                break;
            }
        }
    }
    @NonNull
    @Override
    public h2hChildToAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.h2h_list_layout_from,parent,false);
        return new h2hChildToAdapter.viewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull h2hChildToAdapter.viewHolder holder, int position) {
        holder.getViewTitle().setText(Title);
        h2h_data_from data=data_to.get(position);
        String weight_price="";
        String person_price="";
        float weight_discount;
        if(weightFlag && position==0){
            weight_discount=10.0f;
        }
        else{
           weight_discount=0.0f;
        }
        priceUtilsh2h pru=new priceUtilsh2h(price_all);

        float pri= price_all.get("multi_person_h2h").get(0).getPrice()-weight_discount;
        person_price="(+Tk."+ pri+")";

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
        return data_to.size();
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

        public TextView getViewCAmount() {
            return viewCAmount;
        }

        public TextView getViewCPayer() {
            return viewCPayer;
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

