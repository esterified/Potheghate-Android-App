package com.example.potheghate.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Model.UserProfile;
import com.example.potheghate.Model.admin_o_data;
import com.example.potheghate.R;
import com.example.potheghate.utils.DismissablePopupWindow;

import java.util.ArrayList;
import java.util.HashMap;


public class adminOrderAdapter extends RecyclerView.Adapter<adminOrderAdapter.viewHolder> {
    private final ArrayList<String> data_main;
    private final HashMap<String, UserProfile> data_user;
    private final ArrayList<admin_o_data> data_order;
    public static final String TAG="dash";

    public adminOrderAdapter(ArrayList<String> data_main, HashMap<String, UserProfile> data_user, ArrayList<admin_o_data> data_order) {
        this.data_main = data_main;
        this.data_user = data_user;
        this.data_order = data_order;
    }


    @NonNull
    @Override
    public adminOrderAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_list_order, parent, false);
        return new adminOrderAdapter.viewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull adminOrderAdapter.viewHolder holder, int position) {


        DismissablePopupWindow popupWindow = new DismissablePopupWindow(holder.getViewUser().getContext(), data_user.get(data_main.get(position)));
        SpannableStringBuilder st = new SpannableStringBuilder(data_user.get(data_main.get(position)).getPhone());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View v) {
                popupWindow.show(v,-20,-5);
                Log.d(TAG, "onClick: ");
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);

            }
        };
        st.setSpan(clickableSpan, 0, st.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.getViewUser().setText(st);
        holder.getViewUser().setMovementMethod(LinkMovementMethod.getInstance());
        holder.getViewUser().setHighlightColor(Color.TRANSPARENT);
        holder.getViewPrice().setText("Tk." + data_order.get(0).getPrice());
        if (position == 0) {
            holder.getViewHeader().setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return data_main.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView viewUser;
        private final TextView viewUserName;
        private final TextView viewUserPhone;
        private final TextView viewUserAddress;
        private final TextView viewUserEmail;
        private final TextView viewOrder;
        private final TextView viewPrice;
        private final LinearLayout viewHeader;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            this.viewUser = itemView.findViewById(R.id.ao_user);
            this.viewUserName = itemView.findViewById(R.id.ao_user_name);
            this.viewUserPhone = itemView.findViewById(R.id.ao_user_phone);
            this.viewUserAddress = itemView.findViewById(R.id.ao_user_address);
            this.viewUserEmail = itemView.findViewById(R.id.ao_user_email);
            this.viewHeader = itemView.findViewById(R.id.admin_order_header);
            this.viewOrder = itemView.findViewById(R.id.ao_orderid);
            this.viewPrice = itemView.findViewById(R.id.ao_price);

        }


        public TextView getViewUserName() {
            return viewUserName;
        }

        public TextView getViewUserPhone() {
            return viewUserPhone;
        }

        public TextView getViewUserAddress() {
            return viewUserAddress;
        }

        public TextView getViewUserEmail() {
            return viewUserEmail;
        }

        public TextView getViewOrder() {
            return viewOrder;
        }

        public TextView getViewPrice() {
            return viewPrice;
        }

        public LinearLayout getViewHeader() {
            return viewHeader;
        }

        public TextView getViewUser() {
            return viewUser;
        }
    }
}
