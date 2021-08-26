package com.example.potheghate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.potheghate.Model.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profileActivity extends AppCompatActivity {
    private static final String TAG = "profile";
    public UserProfile value_p;

    EditText nameval, emailval, addressval, phoneval;
    Button cancel, nameedit, emailedit, addressedit, updatebutton, phoneedit;
    String address, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameval = findViewById(R.id.nameTextVal);
        emailval = findViewById(R.id.emailTextVal);
        phoneval = findViewById(R.id.phoneTextVal);
        addressval = findViewById(R.id.addressTextVal);
        cancel = findViewById(R.id.cancel);
        nameedit = findViewById(R.id.buttonNameEdit);
        emailedit = findViewById(R.id.buttonEmailEdit);
        phoneedit = findViewById(R.id.buttonPhoneEdit);
        addressedit = findViewById(R.id.buttonAddressEdit);
        updatebutton = findViewById(R.id.testbutt);

              if(getIntent().getStringExtra("val")!=null){
                  LinearLayout notify=findViewById(R.id.notifyTextView);
                  notify.setVisibility(View.VISIBLE);
              }
        updatebutton.setEnabled(false);
        collectDB();
        nameedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!nameval.isEnabled()) {
                    nameedit.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_baseline_edit_off_24));
                    nameval.setEnabled(true);
                    nameval.requestFocus();

                } else {
                    nameedit.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_baseline_mode_edit_24));
                    nameval.setEnabled(false);

                }
            }
        });
        emailedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!emailval.isEnabled()) {
                    emailedit.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_baseline_edit_off_24));
                    emailval.setEnabled(true);
                    emailval.requestFocus();

                } else {
                    emailedit.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_baseline_mode_edit_24));
                    emailval.setEnabled(false);
                }
            }
        });
        addressedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!addressval.isEnabled()) {
                    addressedit.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_baseline_edit_off_24));
                    addressval.setEnabled(true);
                    addressval.requestFocus();

                } else {
                    addressedit.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_baseline_mode_edit_24));
                    addressval.setEnabled(false);
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    updateInfo();
                    if(getIntent().getStringExtra("val").equals("courier")){
                        finish();
                        startActivity(new Intent(getApplicationContext(), courierActivity_h2c.class));

                    }
                    else if(getIntent().getStringExtra("val").equals("courier_c2h")){
                        finish();
                        startActivity(new Intent(getApplicationContext(), courierActivity_c2h.class));
                    }
                    else if(getIntent().getStringExtra("val").equals("courier_h2h")){
                        finish();
                        startActivity(new Intent(getApplicationContext(), courierActivity_h2h.class));
                    }

                } else {
                    Snackbar.make(v, "update Error", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isObjectNull(UserProfile value) {
        if (value.getname() == null || value.getaddress() == null || value.getemail() == null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validate() {
        boolean m;

        address = addressval.getText().toString().trim();
        name = nameval.getText().toString().trim();
        email = emailval.getText().toString().trim();
        if (address.isEmpty() || name.isEmpty() || email.isEmpty()) {
            if (address.isEmpty()) {
                addressval.setError("address is empty");
            }
            if (name.isEmpty()) {
                nameval.setError("name is empty");
            }
            if (email.isEmpty()) {
                emailval.setError("email is empty");
            }
            m = false;
        } else {
            m = true;
        }

        return m;
    }

    private void collectDB() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        // Read from the database
        myRef.child("users/" + user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.child("address").exists()){
                    addressval.setEnabled(true);
                    addressval.requestFocus();
                    addressedit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_edit_off_24));
                }
                if(!dataSnapshot.child("email").exists()){
                    emailval.setEnabled(true);
                    emailval.requestFocus();
                    emailedit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_edit_off_24));
                }
                if(!dataSnapshot.child("name").exists()){
                    nameval.setEnabled(true);
                    nameval.requestFocus();
                    nameedit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_edit_off_24));
                }

                if (dataSnapshot.exists()) {
                    value_p = dataSnapshot.getValue(UserProfile.class);
                    createCard(value_p.getname(), value_p.getemail(), value_p.getaddress(), user.getPhoneNumber());
                    updatebutton.setEnabled(true);
                }
                else {
                    myRef.child("users/" + user.getUid()).child("phone").setValue(user.getPhoneNumber());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Realtime failure.", Toast.LENGTH_SHORT).show();
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void createCard(String name, String email, String address, String phone) {
        nameval.setText(name);
        emailval.setText(email);
        addressval.setText(address);
        phoneval.setText(phone);


    }

    private void updateInfo() {
        updatebutton.setEnabled(false);
        address = addressval.getText().toString().trim();
        name = nameval.getText().toString().trim();
        email = emailval.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

            value_p.setEmail(email);
            value_p.setAddress(address);
            value_p.setName(name);

            myRef.child("users/" + user.getUid()).setValue(value_p).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        Snackbar.make(nameedit,"info updated successfully",Snackbar.LENGTH_SHORT).show();
                    }
                      else{
                        Snackbar.make(nameedit,"update failed",Snackbar.LENGTH_SHORT).show();
                    }
                    updatebutton.setEnabled(true);
                }
            });



    }


}
