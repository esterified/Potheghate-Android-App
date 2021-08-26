package com.example.potheghate.utils;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.potheghate.R;

public class setEditTextOutsideClickableUI {
    private final Activity MyActivity;
    private final CoordinatorLayout parent;
    ///if this id exists in the package;
    private static final int bottomAppbarId=R.id.bottomButtonPanel;
    private final boolean isCheckKeyboard;

    public setEditTextOutsideClickableUI(Activity myActivity, CoordinatorLayout parent,boolean isCheckKeyboard) {
        this.MyActivity = myActivity;
        this.parent = parent;
        this.isCheckKeyboard=isCheckKeyboard;
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MyActivity);
                    //to remove focus
                    //parent.requestFocus();
                    if(isCheckKeyboard){
                        parent.findViewById(bottomAppbarId).setVisibility(View.VISIBLE);
                    }

                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );

        }
    }
}
