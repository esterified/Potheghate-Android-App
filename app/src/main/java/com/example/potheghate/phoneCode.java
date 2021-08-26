package com.example.potheghate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

public class phoneCode extends AppCompatActivity {
    private EditText phnnumberView;
//--------------------------------------------------------------------------------------------
    //CODE FOR PHONE VERIFICATION
    //needs no password just phone number and OTP verification everytime
//-------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_code);

        // details=getIntent().getStringExtra("phoneNumber");
        //phoneNumber = "+8801752186844";
        phnnumberView = findViewById(R.id.regPhnCode);

        Button butts = findViewById(R.id.regButtonCode);
        isLoggedIn();

        butts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phn=phnnumberView.getText().toString();
                if(phn.length() == 11){
                    phn="+88"+phn;
                    Intent intent=new Intent(getApplicationContext(),phoneCodeOtp.class);
                    intent.putExtra("phone",phn);
                    startActivity(intent);
                    finish();

                }
                else{
                    phnnumberView.setError("Enter a valid phone number");
                }

            }
        });


    }


   private void isLoggedIn(){
       FirebaseAuth a=FirebaseAuth.getInstance();
       FirebaseUser user=a.getCurrentUser();
       if(user!=null){
           Intent nr = new Intent(getApplicationContext(), dashboardActivity.class);
           startActivity(nr);
           finish();
       }
   }


    private void update_name(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }

    private void update_email(FirebaseUser user, String email) {


        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }
}