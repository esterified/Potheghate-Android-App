package com.example.potheghate.Adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Model.parcel;
import com.example.potheghate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class c2hToAdapter extends RecyclerView.Adapter<c2hToAdapter.viewHolder> {
    public static final String TAG = "courier";
    private final ArrayList<String> data;
    private final boolean fromMultiple;
    public HashMap<String, EditText> view_name = new HashMap<>();
    public HashMap<String, EditText> view_address = new HashMap<>();
    public HashMap<String, EditText> view_phone = new HashMap<>();
    public HashMap<String, EditText> view_quantity = new HashMap<>();
    public HashMap<String, EditText> view_details = new HashMap<>();
    public HashMap<String, String> spinnerValW = new HashMap<>();
    public HashMap<String, Boolean> condition = new HashMap<>();
    public HashMap<String, EditText> view_cond_amnt = new HashMap<>();
    private parcel p;

    public c2hToAdapter(ArrayList<String> data, boolean fromMultiple, parcel p) {
        this.data = data;
        this.fromMultiple = fromMultiple;
        this.p = p;

    }

    public void setPNull() {
        this.p = null;
    }

    @NonNull
    @Override
    public c2hToAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courier_from_template, parent, false);
        return new viewHolder(view, spinnerValW, condition, fromMultiple,view_cond_amnt);
    }

    @Override
    public void onBindViewHolder(@NonNull c2hToAdapter.viewHolder holder, int position) {

        if (p != null) {
            holder.getNameView().setText(p.getName());
            holder.getAddressView().setText(p.getAddress());
            holder.getPhoneView().setText(p.getPhone().split("\\+88")[1]);
        }
        this.view_name.put(String.valueOf(position), holder.getNameView());
        this.view_address.put(String.valueOf(position), holder.getAddressView());
        this.view_phone.put(String.valueOf(position), holder.getPhoneView());
        this.view_quantity.put(String.valueOf(position), holder.getViewQuantity());
        this.view_details.put(String.valueOf(position), holder.getViewDetails());
        this.condition.put(String.valueOf(position), false);
        holder.getCountView().setText(String.valueOf(position + 1));
        if (position == 0) {
            holder.getRemoveView().setVisibility(View.GONE);
        } else {
            holder.getRemoveView().setVisibility(View.VISIBLE);
            clickFunc(holder.getRemoveView(), position);

        }

    }

    public void clickFunc(TextView t, int position) {
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, data.size() - position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final HashMap<String, String> spinnerValW;
        private final ArrayList<String> spinnerDataW = new ArrayList<>();
        private final EditText nameView;
        private final EditText phoneView;
        private final EditText addressView;
        private final TextView removeView;
        private final TextView countView;
        private final EditText viewQuantity;
        private final EditText viewDetails;
        private final RadioGroup fromGroup;
        private final HashMap<String, Boolean> condition;
        private final HashMap<String, EditText> view_cond_amnt;
        private final LinearLayout condSection;
        private final EditText viewCondAmount;
        AdapterView.OnItemSelectedListener spinnerListenerW = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sp = parent.getItemAtPosition(position).toString();
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    spinnerValW.put(String.valueOf(getAdapterPosition()), sp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        RadioGroup.OnCheckedChangeListener condListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                condition.put(String.valueOf(getAdapterPosition()), checkedId != R.id.fromGroup_1_c);
                if(checkedId!=R.id.fromGroup_1_c){
                    condSection.setVisibility(View.VISIBLE);
                    view_cond_amnt.put(String.valueOf(getAdapterPosition()),viewCondAmount);
                }
                else {
                    condSection.setVisibility(View.GONE);
                }
                //Log.d(TAG, "onCheckedChanged: ");
            }
        };

        public viewHolder(@NonNull View itemView, HashMap<String, String> spinnerVal, HashMap<String, Boolean> condition, boolean fromMultiple,HashMap<String, EditText> view_cond_amnt) {
            super(itemView);
            this.condition = condition;
            this.view_cond_amnt=view_cond_amnt;
            this.viewCondAmount = itemView.findViewById(R.id.condYesEditText);
            this.condSection=itemView.findViewById(R.id.condYes);
            this.nameView = itemView.findViewById(R.id.fromName);
            this.phoneView = itemView.findViewById(R.id.fromPhone);
            this.addressView = itemView.findViewById(R.id.fromAddress);
            this.removeView = itemView.findViewById(R.id.fromRemove);
            this.countView = itemView.findViewById(R.id.fromCount);
            this.viewQuantity = itemView.findViewById(R.id.productQuantity);
            this.viewDetails = itemView.findViewById(R.id.detailsPView);
            Spinner spinner = itemView.findViewById(R.id.weightSpinner);
            LinearLayout productLayout = itemView.findViewById(R.id.courierFromP);
            this.fromGroup = itemView.findViewById(R.id.fromGroup_c);
            this.spinnerValW = spinnerVal;
            if (fromMultiple) {
                addSpinnerDataMaterial(spinner);
                productLayout.setVisibility(View.VISIBLE);
                fromGroup.setOnCheckedChangeListener(condListener);
            } else {
                productLayout.setVisibility(View.GONE);
            }


        }

        public RadioGroup getFromGroup() {
            return fromGroup;
        }

        private void addSpinnerDataMaterial(Spinner spinner) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("courier/courier_material");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            String s = sn.child("name").getValue(String.class);
                            Float f = sn.child("price").getValue(Float.class);
                            spinnerDataW.add(s+"(+Tk."+f+")");
                        }
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(spinner.getContext(), android.R.layout.simple_spinner_item, spinnerDataW);
                        Resources.Theme theme=spinner.getContext().getTheme();
                        theme.applyStyle(R.style.spinnerText,true);
                        spinnerAdapter.setDropDownViewTheme(theme);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerAdapter);
                        spinner.setOnItemSelectedListener(spinnerListenerW);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public EditText getViewQuantity() {
            return viewQuantity;
        }

        public EditText getViewDetails() {
            return viewDetails;
        }

        public TextView getRemoveView() {
            return removeView;
        }

        public TextView getCountView() {
            return countView;
        }

        public EditText getNameView() {
            return nameView;
        }

        public EditText getPhoneView() {
            return phoneView;
        }

        public EditText getAddressView() {
            return addressView;
        }

    }
}
