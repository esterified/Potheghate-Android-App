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
import com.example.potheghate.courierActivity_c2h;

import com.example.potheghate.utils.priceUtilsc2h;

import java.util.ArrayList;
import java.util.HashMap;


public class c2hChildToAdapter extends RecyclerView.Adapter<c2hChildToAdapter.viewHolder>{
    private final ArrayList<c_data_to> data_to;
    private final ArrayList<c_data_from> data_from;
    private final HashMap<String, ArrayList<c_price>> price_all ;
    private final HashMap<String, ArrayList<c_price>> price_all_courier ;
    private boolean weightFlag=false;
    public c2hChildToAdapter(ArrayList<c_data_to> data_to,HashMap<String, ArrayList<c_price>> price_all,HashMap<String, ArrayList<c_price>> price_all_courier,ArrayList<c_data_from> data_from) {
        this.data_to = data_to;
        this.price_all=price_all;
        this.price_all_courier=price_all_courier;
        this.data_from=data_from;
        weightLoop();
    }

    @NonNull
    @Override
    public c2hChildToAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.h2c_list_layout_to,parent,false);
        return new c2hChildToAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull c2hChildToAdapter.viewHolder holder, int position) {
        holder.getViewTitle().setText(new String("From"));
        c_data_to data=data_to.get(position);
        String weight_price="";
        String quantity_price="";
        String courier_price="";
        String condition_price="";
        String condition_amount_value="";
        float weight_discount=0.0f;

        priceUtilsc2h pru=new priceUtilsc2h(price_all,price_all_courier);
        if(weightFlag && position==0){
            weight_discount=10.0f;

        }
        if(data_to.size()<=1 ){
            float pri=price_all.get("courier_price").get(0).getPrice()-weight_discount;
            courier_price="(+Tk."+ pri+")";
        }
        else {
            float pri=pru.binning(data_to).get(position)-weight_discount;
            courier_price="(+Tk."+ pri+")";
        }
        String packing_price="(+Tk."+ pru.calcPackagePrice(data.getCourier_package(),data.getCourier_service())+")";
        holder.getViewCourier().setText(new String("Courier: ").concat(data.getCourier_service()).concat(courier_price));
        holder.getViewRDetails().setText(new String("Receiver: ").concat(data.getReceiver_details()));
        holder.getViewPackage().setText(new String("Packing: ").concat(data.getCourier_package()).concat(packing_price));
        if(data.getProduct_weight()!=null){
            weight_price="(+Tk."+pru.calcMaterialPrice(data.getProduct_weight())+")";
            holder.getViewWeight().setText(new String("Weight: ").concat(data.getProduct_weight()).concat(weight_price));
            holder.getViewPDetails().setText(new String("Product: ").concat(data.getProduct_details()));
            holder.getViewQuantity().setText(new String("Quantity: ").concat(data.getProduct_quantity()).concat(quantity_price));
            if(data.getCondition().equals("Yes")){
                condition_amount_value="("+data.getCondtion_amount()+") ";
                condition_price="(+Tk."+pru.calcConditionPrice(data.getCondition(),data.getCondtion_amount())+")";
            }
            holder.getViewCond().setText(new String("Condition: ").concat(data.getCondition()).concat(condition_amount_value).concat(condition_price));
        }

    }
    private void weightLoop(){
        for(c_data_to t:data_to){
            if(t.getProduct_weight()!=null && !t.getProduct_weight().equals("5kg or less")){
                weightFlag=true;
                break;
            }
        }
        for(c_data_from a:data_from){
            if(a.getProduct_weight()!=null && !a.getProduct_weight().equals("5kg or less")){
                weightFlag=true;
                break;
            }
        }
    }
    @Override
    public int getItemCount() {
        return data_to.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView viewRDetails;
        private final TextView viewCourier;
        private final TextView viewQuantity;
        private final TextView viewPDetails;
        private final TextView viewWeight;
        private final TextView viewPackage;
        private final TextView viewCond;
        private final TextView viewTitle;

        public TextView getViewTitle() {
            return viewTitle;
        }

        public TextView getViewCond() {
            return viewCond;
        }

        public TextView getViewPackage() {
            return viewPackage;
        }

        public TextView getViewRDetails() {
            return viewRDetails;
        }

        public TextView getViewCourier() {
            return viewCourier;
        }

        public TextView getViewQuantity() {
            return viewQuantity;
        }

        public TextView getViewPDetails() {
            return viewPDetails;
        }

        public TextView getViewWeight() {
            return viewWeight;
        }


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            this.viewRDetails=itemView.findViewById(R.id.tll_r_details);
            this.viewCourier=itemView.findViewById(R.id.tll_courier);
            this.viewQuantity=itemView.findViewById(R.id.tll_p_quantity);
            this.viewPDetails=itemView.findViewById(R.id.tll_p_details);
            this.viewWeight=itemView.findViewById(R.id.tll_p_weight);
            this.viewPackage=itemView.findViewById(R.id.tll_package);
            this.viewCond=itemView.findViewById(R.id.tll_p_cond);
            this.viewTitle=itemView.findViewById(R.id.tll_title);

        }
    }
}
