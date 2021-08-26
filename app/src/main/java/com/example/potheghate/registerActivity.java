
package com.example.potheghate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.potheghate.Model.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registerActivity extends AppCompatActivity {
    private EditText rName,rPass,rPassC,rPhone,rEmail,rAdr;
    private TextView login;
    private Button reg;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        init_views();
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    //upload to database
                    createAccount(mAuth);
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x= new Intent(registerActivity.this, LoginActivity.class);
                startActivity(x);
            }
        });

    }
    public void gotoMain(){
        Intent x= new Intent(registerActivity.this, LoginActivity.class);
        startActivity(x);
    }
    private void createAccount(FirebaseAuth x){
        String vrEmail=rEmail.getText().toString().trim();
        String vrPass=rPass.getText().toString().trim();
        String vrName=rName.getText().toString().trim();
        String vrAdr=rAdr.getText().toString().trim();
        String vrPhone=rPhone.getText().toString().trim();
        ProgressBar bar=findViewById(R.id.progressBarreg);
        bar.setVisibility(View.VISIBLE);
        x.createUserWithEmailAndPassword(vrEmail, vrPass)
                .addOnCompleteListener(registerActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            insertDB(x,vrName,vrPhone,vrAdr);
                            update_name(user,vrName);
                            bar.setVisibility(View.GONE);

                            //updateUI(user);
                        } else {
                            bar.setVisibility(View.GONE);
                            try {
                                throw task.getException();
                            } catch(Exception e) {
                                String error=e.getMessage();
                                Snackbar.make(reg, error,
                                        Snackbar.LENGTH_LONG).show();

                            }

                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(registerActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    private void init_views(){
        rName=findViewById(R.id.regName);
        rPass=findViewById(R.id.regPassword);
        rPassC=findViewById(R.id.regPasswordC);
        rPhone=findViewById(R.id.regPhn);
        rEmail=findViewById(R.id.regEmail);
        rAdr=findViewById(R.id.regAddress);
        login=findViewById(R.id.nLogin);
        reg=findViewById(R.id.regButton);

    }
    private boolean validate(){
        String vrName=rName.getText().toString();
        String vrPass=rPass.getText().toString();
        String vrPassC=rPassC.getText().toString();
        String vrEmail=rEmail.getText().toString();
        String vrAdr=rAdr.getText().toString();
        String vrPhone=rPhone.getText().toString();

        if(vrName.isEmpty() || vrPass.isEmpty() || vrPassC.isEmpty() ||vrPhone.isEmpty() ||vrEmail.isEmpty() ||vrAdr.isEmpty() ){
            Toast.makeText(this,"Please enter all values",Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            if(!vrPass.equals(vrPassC)){
                Toast.makeText(this,"Password do not match",Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

    }

    private void update_name(FirebaseUser user,String name ){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            gotoMain();
                        }
                    }
                });
    }
    private void update_email(FirebaseUser user,String email){


        user.updateEmail(email);
    }
    private void insertDB(FirebaseAuth x1,String Name,String Phone,String address){
        // Write a message to the database
        UserProfile userinfo=new UserProfile(Name,Phone,address);
        FirebaseUser user = x1.getCurrentUser();


        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        myRef.setValue(userinfo).addOnCompleteListener(registerActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(Register.this, "Realtime Success."+user.getUid(),
                           // Toast.LENGTH_SHORT).show();


                }
                else{
                    //Toast.makeText(Register.this, "Realtime failure.",
                            //Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
