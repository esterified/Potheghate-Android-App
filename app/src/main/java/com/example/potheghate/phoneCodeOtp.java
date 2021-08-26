package com.example.potheghate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.potheghate.utils.progressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class phoneCodeOtp extends AppCompatActivity {
    private static final String TAG = "code";
    private Button confirm;
    private FirebaseAuth mAuth;
    private String mVerificationId;
   // private ProgressBar p_bar;
    private EditText Codeview;
    private LinearLayout lin;
    private PhoneAuthProvider.ForceResendingToken Token;
    private progressBar progBar;
    //--------------------------------------------------------------------------------------------
    //CODE FOR PHONE VERIFICATION
    //needs no password just phone number and OTP verification everytime
    //-------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progBar=new progressBar(this);
        progBar.show();
        setContentView(R.layout.activity_phone_code_otp);
        Codeview = findViewById(R.id.regPhnOTP);
        confirm = findViewById(R.id.buttonC);
        //p_bar = findViewById(R.id.progressBar);
        TextView resend=findViewById(R.id.viewResend);
        mAuth = getInstance();
        confirm.setEnabled(false);
       // p_bar.setVisibility(View.VISIBLE);
        String phoneNumber=getIntent().getStringExtra("phone");
        phoneAuthStart(phoneNumber);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t=Codeview.getText().toString();
                if(!t.isEmpty()){
                    SignInWithCode(t);
                }

            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phoneNumber,Token);
            }
        });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            //Log.d(TAG, "onVerificationCompleted:" + credential);
            //p_bar.setVisibility(View.GONE);
            progBar.dismiss();
            signInWithPhoneAuthCredential(credential);
            Log.d(TAG, "onVerificationCompleted: ");
            Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
          //  p_bar.setVisibility(View.GONE);
            progBar.dismiss();
            Log.d(TAG, "verification failed ");
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent: ");
            //Toast.makeText(getApplicationContext(), "CodeSent", Toast.LENGTH_SHORT).show();

                confirm.setEnabled(true);
               // p_bar.setVisibility(View.GONE);
                progBar.dismiss();
                mVerificationId = s;
                Token=forceResendingToken;
                //String code = Codeview.getText().toString();


        }
        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
        }
    };
    private void phoneAuthStart(String phoneNumber) {
        Log.d(TAG, "verify: ");

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(phoneCodeOtp.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)       // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Toast.makeText(getApplicationContext(), "Verifying....", Toast.LENGTH_SHORT).show();

    }
    private void SignInWithCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        Log.d(TAG, code);
        if(credential.getSmsCode()!=null) {
            Log.d(TAG, credential.getSmsCode());
        }
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = getInstance();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(phoneCodeOtp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String a=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                            String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            //migrate the phone number info from admin base to realtime database
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("phone").setValue(a);
                            Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+a);
                            startActivity(new Intent(getApplicationContext(), dashboardActivity.class));

                        }
                        else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "Sign in Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(phoneCodeOtp.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)       // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
