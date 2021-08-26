package com.example.potheghate.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.potheghate.Model.UserProfile;
import com.example.potheghate.R;

public class DismissablePopupWindow extends android.widget.PopupWindow {
    Context ctx;
    Button btnDismiss;
    TextView viewName;
    TextView viewEmail;
    TextView viewPhone;
    TextView viewAddress;
    // TextView lblText;
    View popupView;

    public DismissablePopupWindow(Context context, UserProfile U) {
        super(context);

        ctx = context;
        popupView = LayoutInflater.from(context).inflate(R.layout.popup_layout,
                null);

        setContentView(popupView);

        btnDismiss =  popupView.findViewById(R.id.dismiss);
        viewName =  popupView.findViewById(R.id.ao_user_name);
        viewEmail =  popupView.findViewById(R.id.ao_user_email);
        viewPhone =  popupView.findViewById(R.id.ao_user_phone);
        viewAddress =  popupView.findViewById(R.id.ao_user_address);
        viewName.setText(U.getname());
        viewEmail.setText(U.getemail());
        viewPhone.setText(U.getPhone());
        viewPhone.setVisibility(View.GONE);
        viewAddress.setText(U.getaddress());
        // lblText = (TextView)popupView.findViewById(R.id.terms_conditions);

        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(200);

        // Closes the popup window when touch outside of it - when looses focus
        setOutsideTouchable(true);
        setFocusable(true);


        // Removes default black background
        setBackgroundDrawable(ContextCompat.getDrawable(ctx,R.drawable.popup__bg));
        setElevation(10);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                dismiss();

            }
        });

        // Closes the popup window when touch it
        /*
         * this.setTouchInterceptor(new View.OnTouchListener() {
         *
         * @Override public boolean onTouch(View v, MotionEvent event) {
         *
         * if (event.getAction() == MotionEvent.ACTION_MOVE) { dismiss(); }
         * return true; } });
         */
    } // End constructor

    // Attaches the view to its parent anchor-view at position x and y
    public void show(View anchor, int x, int y) {
        showAsDropDown(anchor, x, y);
    }
}
