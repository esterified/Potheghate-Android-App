package com.example.potheghate.Adapter;

import android.content.res.Resources;
import android.util.Log;
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

import com.example.potheghate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class h2cToAdapter extends RecyclerView.Adapter<h2cToAdapter.viewHolder> {
    public static final String TAG = "courier";
    private final ArrayList<String> data;
    private final boolean toMultiple;
    public HashMap<String, EditText> view_quantity = new HashMap<>();
    public HashMap<String, EditText> view_details = new HashMap<>();
    public HashMap<String, EditText> view_rDetails = new HashMap<>();
    public HashMap<String, String> spinnerValW = new HashMap<>();
    public HashMap<String, String> spinnerValC = new HashMap<>();
    public HashMap<String, String> spinnerValP = new HashMap<>();
    public HashMap<String, Boolean> condition = new HashMap<>();
    public HashMap<String, EditText> view_cond_amnt = new HashMap<>();

    public h2cToAdapter(ArrayList<String> data, boolean toMultiple) {
        this.data = data;
        this.toMultiple = toMultiple;
    }

    @NonNull
    @Override
    public h2cToAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courier_to_template, parent, false);
        return new h2cToAdapter.viewHolder(view, spinnerValC, spinnerValW, spinnerValP, condition, toMultiple, view_cond_amnt);
    }

    @Override
    public void onBindViewHolder(@NonNull h2cToAdapter.viewHolder holder, int position) {
        this.view_quantity.put(String.valueOf(position), holder.getViewQuantity());
        this.view_details.put(String.valueOf(position), holder.getViewDetails());
        this.view_rDetails.put(String.valueOf(position), holder.getViewRDetails());
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

        private final EditText viewRDetails;
        private final HashMap<String, String> spinnerValC;
        private final HashMap<String, String> spinnerValW;
        private final HashMap<String, String> spinnerValP;
        private final HashMap<String, Boolean> condition;
        private final HashMap<String, EditText> view_cond_amnt;
        private final ArrayList<String> spinnerDataC = new ArrayList<>();
        private final ArrayList<String> spinnerDataW = new ArrayList<>();
        private final ArrayList<String> spinnerDataP = new ArrayList<>();
        private final TextView removeView;
        private final TextView countView;
        Spinner spinnerP;
        //--------------------------
        private final EditText viewQuantity;
        private final EditText viewDetails;
        private final EditText viewCondAmount;
        private final LinearLayout condSection;


        AdapterView.OnItemSelectedListener spinnerListenerC = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sp = parent.getItemAtPosition(position).toString();
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    Log.d(TAG, "spinner ");
                    spinnerValC.put(String.valueOf(getAdapterPosition()), sp);
                    //addSpinnerDataLocation(spinnerL,sp);
                    addSpinnerDataPackage(spinnerP,sp);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };


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
        AdapterView.OnItemSelectedListener spinnerListenerP = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sp = parent.getItemAtPosition(position).toString();
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    spinnerValP.put(String.valueOf(getAdapterPosition()), sp);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        RadioGroup.OnCheckedChangeListener condListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                condition.put(String.valueOf(getAdapterPosition()), checkedId != R.id.toGroup_1_c);
                if(checkedId!=R.id.toGroup_1_c){
                    condSection.setVisibility(View.VISIBLE);
                    view_cond_amnt.put(String.valueOf(getAdapterPosition()),viewCondAmount);
                }
                else {
                    condSection.setVisibility(View.GONE);
                }

            }
        };

        public viewHolder(@NonNull View itemView, HashMap<String, String> spinnerValC, HashMap<String,
                String> spinnerValW, HashMap<String, String> spinnerValP, HashMap<String, Boolean> condition, boolean toMultiple,HashMap<String, EditText> view_cond_amnt) {
            super(itemView);

            this.viewRDetails = itemView.findViewById(R.id.toRDetails);
            this.removeView = itemView.findViewById(R.id.fromRemove);
            this.countView = itemView.findViewById(R.id.fromCount);
            this.viewQuantity = itemView.findViewById(R.id.productQuantity);
            this.viewDetails = itemView.findViewById(R.id.detailsPView);
            RadioGroup toGroup = itemView.findViewById(R.id.toGroup_c);
            Spinner spinnerC = itemView.findViewById(R.id.courierServiceSpinner);
            this.spinnerP = itemView.findViewById(R.id.courierPackageSpinner);
            Spinner spinnerW = itemView.findViewById(R.id.weightSpinner);

            LinearLayout productLayout = itemView.findViewById(R.id.courierToP);
            this.spinnerValC = spinnerValC;
            this.spinnerValW = spinnerValW;
            this.spinnerValP = spinnerValP;
            this.condition = condition;
            this.view_cond_amnt=view_cond_amnt;
            this.viewCondAmount = itemView.findViewById(R.id.condYesEditText);
            this.condSection=itemView.findViewById(R.id.condYes);
            this.removeView.setVisibility(View.GONE);
            if (toMultiple) {
                toGroup.setOnCheckedChangeListener(condListener);
                addSpinnerDataMaterial(spinnerW);
                productLayout.setVisibility(View.VISIBLE);
            } else {
                productLayout.setVisibility(View.GONE);
            }
            addSpinnerDataService(spinnerC);
            // addSpinnerDataPackage(spinnerP);
            // addSpinnerDataLocation(spinnerL);

        }

        public TextView getRemoveView() {
            return removeView;
        }

        public TextView getCountView() {
            return countView;
        }

        public EditText getViewQuantity() {
            return viewQuantity;
        }

        public EditText getViewDetails() {
            return viewDetails;
        }

        public EditText getViewRDetails() {
            return viewRDetails;
        }

        private void addSpinnerDataService(Spinner spinner) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("courier/courier_service");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            String s = sn.child("name").getValue(String.class);
                            spinnerDataC.add(s);
                        }
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(spinner.getContext(), android.R.layout.simple_spinner_item, spinnerDataC);
                        Resources.Theme theme=spinner.getContext().getTheme();
                        theme.applyStyle(R.style.spinnerText,true);
                        spinnerAdapter.setDropDownViewTheme(theme);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerAdapter);
                        spinner.setOnItemSelectedListener(spinnerListenerC);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void addSpinnerDataMaterial(Spinner spinner) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("courier/courier_material");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            String s = sn.child("name").getValue(String.class);
                            Float f =  sn.child("price").getValue(Float.class);
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

        private void addSpinnerDataPackage(Spinner spinner,String courier_name) {
            if(spinnerDataP.size()>=1){spinnerDataP.clear();}
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("courier/courier_service");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            String s = sn.child("name").getValue(String.class);
                            if(s.equals(courier_name)){
                               for(DataSnapshot d:sn.child("package").getChildren()){
                                   String temp=d.getKey()+"(+Tk."+d.getValue(Float.class)+")";
                                   spinnerDataP.add(temp);
                               }
                            }
                        }
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(spinner.getContext(), android.R.layout.simple_spinner_item, spinnerDataP);
                        Resources.Theme theme=spinner.getContext().getTheme();
                        theme.applyStyle(R.style.spinnerText,true);
                        spinnerAdapter.setDropDownViewTheme(theme);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerAdapter);
                        spinner.setOnItemSelectedListener(spinnerListenerP);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
