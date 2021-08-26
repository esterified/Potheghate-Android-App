package com.example.potheghate;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout nameL,passL;
    public static String TAG="error";
    private FirebaseAuth a;
    ProgressBar prog_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        a=FirebaseAuth.getInstance();
        FirebaseUser user=a.getCurrentUser();
        if(user!=null){
            Intent nr = new Intent(getApplicationContext(), dashboardActivity.class);
            startActivity(nr);
            finish();
        }
        Button butt;
        TextView reg;
        nameL= findViewById(R.id.nameL);
        passL=findViewById(R.id.passL);
        butt= findViewById(R.id.inputButton);
        reg=findViewById(R.id.gotoReg);
        prog_=findViewById(R.id.progressBarde);


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent r=new Intent(getApplicationContext(), registerActivity.class);
               startActivity(r);
            }
        });

        butt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard(LoginActivity.this);
                prog_.setVisibility(View.VISIBLE);
                String em= Objects.requireNonNull(nameL.getEditText()).getText().toString().trim();
                String pass= Objects.requireNonNull(passL.getEditText()).getText().toString().trim();
                if(nameL.getError()!=null) { nameL.setError(null);}
                if(passL.getError()!=null) { passL.setError(null);}
                if(em.isEmpty()){
                    nameL.setError("name cannot be empty");
                }
                if(pass.isEmpty()){
                    passL.setError("password cannot be empty");
                }
                if(pass.isEmpty() || em.isEmpty()){
                    prog_.setVisibility(View.GONE);
                    return;
                }
                a.signInWithEmailAndPassword(em, pass)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = a.getCurrentUser();
                                    Intent nr = new Intent(getApplicationContext(), dashboardActivity.class);
                                    startActivity(nr);
                                    finish();
                                    prog_.setVisibility(View.GONE);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    if(!task.isSuccessful()) {
                                     getErrorMessage(task,butt);
                                     prog_.setVisibility(View.GONE);
                                    }

                                }

                            }

                        });
            }
        });



    }
private void getErrorMessage(Task<AuthResult> t,Button b){
     String error="error";
    try {
        throw Objects.requireNonNull(t.getException());
    } catch(FirebaseAuthInvalidUserException e) {
       error="Email does not match";
        nameL.setError(error);
    } catch(FirebaseAuthInvalidCredentialsException e) {
      error="Password does not match";
      passL.setError(error);
    }
    catch(Exception e) {
    error=e.getMessage();
    }
    assert error != null;
    Snackbar.make(b, error,
            Snackbar.LENGTH_LONG).show();

}
    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }





}