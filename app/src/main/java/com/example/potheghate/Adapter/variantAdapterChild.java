package com.example.potheghate.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Interface.variantInterface;
import com.example.potheghate.Model.V_Data;
import com.example.potheghate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class variantAdapterChild extends RecyclerView.Adapter<variantAdapterChild.viewHolder> {
    public static final String TAG = "trans";
    private final ArrayList<V_Data> localdataset;
    private final int id;
    private final ArrayList<RadioButton> radioButtonArrayList = new ArrayList<>();
    public static final int LIST_RADIO =R.layout.activity_variant_list_radio;
    public static final int LIST_CHECKBOX =R.layout.activity_variant_list_checkbox;
    variantInterface listener;


    public variantAdapterChild(ArrayList<V_Data> localdataset, int id, variantInterface listener) {
        this.localdataset = localdataset;
        this.id = id;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return this.localdataset.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(id, viewGroup, false);

        return new viewHolder(view,id);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ///--------------animation--------------------
        holder.getCardView().setAnimation(AnimationUtils.loadAnimation(holder.getCardView().getContext(), R.anim.fadeandslide));
        ///--------------animation--------------------

        V_Data data = localdataset.get(position);

        String price = "Tk. +" + data.getPrice().toString();
        holder.getPriceView().setText(price);

        holder.getTitleView().setText(data.getName());


        //----------------------radio button functionalities---------------
        if (id == LIST_RADIO && position!=RecyclerView.NO_POSITION) {
            RadioButton radio = holder.getRadioButton();
            radioFunction(radio, data, position);
        }
        //----------------------checkbox functionalities---------------
        else if (id == LIST_CHECKBOX  && position!=RecyclerView.NO_POSITION) {
            CheckBox checkBox = holder.getCheckBox();
            checkboxFunction(checkBox, data, position);
        }

        //-----------------------------------------------
        if (data.getImage() != null) {

            Picasso.get().load(data.getImage()).fit().centerCrop().into(holder.getImageView());
        }


    }

    private void checkboxFunction(CheckBox checkBox, V_Data data, int position) {

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isChecked()) {

                    listener.putDataCheckbox("ss", data);
                } else {

                    listener.removeDataCheckbox("ss", data);
                }


            }
        });
    }

    private void radioFunction(RadioButton radio, V_Data data, int position) {
        radioButtonArrayList.add(radio);
        radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (RadioButton radio : radioButtonArrayList) {
                    radio.setChecked(false);
                }
                radio.setChecked(true);
                listener.putDataRadio(localdataset.get(position).getSubcatTitle(), data);

            }
        });


        if (position == 0) {
            Log.d(TAG, data.getSubcatTitle() + "");
            radio.setChecked(true);
            listener.putDataRadio(localdataset.get(position).getSubcatTitle(), data);

        }

    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView priceView;
        private final ImageView imageView;
        private final CardView cardView;
        private RadioButton radioButton;
        private CheckBox checkBox;


        public viewHolder(@NonNull View itemView,int id) {
            super(itemView);
            if (id == LIST_RADIO) {
                this.radioButton = itemView.findViewById(R.id.radioButton2);
            }
            //----------------------checkbox functionalities---------------
            else if (id == LIST_CHECKBOX) {
                this.checkBox = itemView.findViewById(R.id.checkBox);
            }


            this.titleView = itemView.findViewById(R.id.listNameV);
            this.priceView = itemView.findViewById(R.id.listPriceV);
            this.imageView = itemView.findViewById(R.id.imageViewList1V);
            this.cardView = itemView.findViewById(R.id.cardRoot1V);

        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public RadioButton getRadioButton() {
            return radioButton;
        }

        public CardView getCardView() {
            return cardView;
        }

        public TextView getTitleView() {
            return titleView;
        }

        public TextView getPriceView() {
            return priceView;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }


}
