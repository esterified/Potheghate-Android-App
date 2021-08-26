package com.example.potheghate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Model.V_Data;
import com.example.potheghate.R;
import com.example.potheghate.Interface.variantInterface;


import java.util.ArrayList;


public class variantAdapter extends RecyclerView.Adapter<variantAdapter.viewHolder> {
   private final ArrayList<String> keyset;
    private final ArrayList<ArrayList<V_Data>> datalist;
    private final ArrayList<String> idList;
    private final variantInterface listener;
    public static final String RADIO="radio";
    public static final String CHECKBOX="checkbox";
    public static final String TAG="nested";


    public variantAdapter(ArrayList<String> keyset, ArrayList<ArrayList<V_Data>> datalist, ArrayList<String> idList,variantInterface listener) {
        this.keyset = keyset;
        this.datalist = datalist;
        this.idList = idList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_variant_parent, viewGroup, false);
        return new variantAdapter.viewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        int ab=R.layout.activity_variant_list_radio;
        int cd=R.layout.activity_variant_list_checkbox;
        int id=ab;
        if(idList.get(position).equals(CHECKBOX)){
            id=cd;
        }
        /*else if(idList.get(position).equals(RADIO)){
            id=ab;
        }*/
        variantAdapterChild adapterV = new variantAdapterChild(datalist.get(position),id,listener);

        holder.getRecyclerView().setAdapter(adapterV);
        holder.getRecyclerView().setVisibility(View.VISIBLE);
        holder.getTextView().setText(keyset.get(position));

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recyclerView;
        private final TextView textView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            this.recyclerView=itemView.findViewById(R.id.RecycleView_list);
            this.textView=itemView.findViewById(R.id.recycler_text);
        }

        public RecyclerView getRecyclerView() {
            return recyclerView;
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
