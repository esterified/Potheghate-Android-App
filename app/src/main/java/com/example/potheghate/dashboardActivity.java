package com.example.potheghate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static java.lang.String.valueOf;

public class dashboardActivity extends AppCompatActivity {
    //Navigation drawer variables
    DrawerLayout nav_draw;
    NavigationView nav_view;
    Toolbar nav_tool;
    RelativeLayout card1, card2,card3,card4;
    public static final String TAG="dash";
    long c_count=0;
    long h2h_count=0;

    //----------------------navbar drawer------------------
    NavigationView.OnNavigationItemSelectedListener navlistener = new NavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (item.getItemId() == R.id.NavLogout) {
                Logout();

            } else if (item.getItemId() == R.id.NavProfile) {
            startActivity(new Intent(getApplicationContext(),profileActivity.class));
            }
            else if (item.getItemId() == R.id.nav_admin) {
                startActivity(new Intent(getApplicationContext(),adminDashboard.class));
            }
            else if (item.getItemId() == R.id.NavWallet) {
            startActivity(new Intent(getApplicationContext(),orderTrackingActivity.class));
            }
            else if (item.getItemId() == R.id.NavDoc) {
                startActivity(new Intent(getApplicationContext(),docSubmitActivity.class));
            }
            else if (item.getItemId() == R.id.nav_dingi) {
                startActivity(new Intent(getApplicationContext(),mapActivity.class));
            }
            else if (item.getItemId() == R.id.nav_contact_us) {
                startActivity(new Intent(getApplicationContext(),supportActivity.class));
            }
            nav_draw.closeDrawer(GravityCompat.START);
            return false;
        }

    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //--------------------------------------------------
        //Date currentTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+06:00")).getTime();
        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);

       // Log.d(TAG, currentTime);
        askPermission();
        checkIfAdmin();
        welcomeWish();
        countCourier();
        countH2h();
        card1 = findViewById(R.id.relative_Shop);
        card2 = findViewById(R.id.relative_Res);
        card3 = findViewById(R.id.relative_Courier);
        card4 = findViewById(R.id.relative_Home2home);
        //----------------------navbar drawer------------------
        nav_draw = findViewById(R.id.nav_draw);
        nav_view = findViewById(R.id.nav_view);
        nav_tool = findViewById(R.id.p_toolbar);
        //----------------------toolbar------------------
        setSupportActionBar(nav_tool);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //-----------------navbar drawer--------------------------
        nav_view.bringToFront();
        nav_view.setNavigationItemSelectedListener(navlistener);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(dashboardActivity.this, nav_draw, nav_tool, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        nav_draw.addDrawerListener(toggle);
        toggle.syncState();
        //-----------------profile visibility-----------------
       // Menu temp = nav_view.getMenu();
       // temp.findItem(R.id.nav_11).setVisible(false);

        nav_tool.setNavigationIcon(R.drawable.nav_icon);
        //----------------------navbar drawer------------------
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), resActivity.class));
            }
        });
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "SHop", Toast.LENGTH_SHORT).show();
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), courierActivity_h2c.class));
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), courierActivity_h2h.class));
            }
        });


    }
    private void checkIfAdmin(){
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
    ref.child("users").child(user.getUid()).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists() && snapshot.getValue(String.class).equals("admin")){
                   Menu m=nav_view.getMenu();
                   m.findItem(R.id.nav_admin).setVisible(true);
               }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    }
    //----------------------navbar drawer------------------
    //------------------------------
    private void countCourier(){
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("courier_h2c").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 long c = snapshot.getChildrenCount();
                ref.child("courier_c2h").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        c_count= c +snapshot.getChildrenCount();
                        TextView t=findViewById(R.id.courier_noti);
                        t.setText(String.valueOf(c_count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
private void countH2h(){
    FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    ref.child("courier_h2h").child(user.getUid()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            h2h_count = snapshot.getChildrenCount();
            TextView t=findViewById(R.id.h2h_noti);
            t.setText(String.valueOf(h2h_count));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

}
    private void welcomeWish(){
        Locale currentLocale = new Locale.Builder().setRegion("BD").build();
        SimpleDateFormat sdf = new SimpleDateFormat("HH", currentLocale);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+06:00"));
        String currentTime = sdf.format(new Date());
        int time=Integer.parseInt(currentTime);
        String wish="";
        if(time>=5&&time<12){
            wish="Good Morning";
        }
        else if(time>=12 && time<17){
            wish="Good Afternoon";
        }
        else if(time>=17 && time<21){
            wish="Good Evening";
        }
        else {
            wish="Its Night time";
        }
        welcome(wish);
    }

    public void welcome(String wishText) {
        TextView welcome = findViewById(R.id.welcome_Text);
        /////////////////////////////
        FirebaseAuth a = FirebaseAuth.getInstance();
        FirebaseUser fUser = a.getCurrentUser();
        String dispName="";
        if(fUser.getDisplayName()!=null){
            dispName=fUser.getDisplayName();
        }

        String t=wishText + " " + dispName+ "!";
        if(fUser!=null) {
            welcome.setText(t);
        }
    }

    @Override
    public void onBackPressed() {
        if (nav_draw.isDrawerOpen(GravityCompat.START)) {
            nav_draw.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void Logout() {
        FirebaseAuth x = FirebaseAuth.getInstance();
        x.signOut();
        startActivity(new Intent(getApplicationContext(), phoneCode.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    public static void getCount(TextView v) {
        FirebaseUser a = FirebaseAuth.getInstance().getCurrentUser();
        assert a != null;
        DatabaseReference dataref = FirebaseDatabase.getInstance().getReference("cart");
        Query query = dataref.child(a.getUid()).orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            String test = "0";

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int sum=0;
                    for(DataSnapshot snap:snapshot.getChildren()){
                        String s=snap.child("quantity").getValue(String.class);
                        assert s != null;
                        if(!s.isEmpty() && !s.equals("0")){
                            sum=sum+Integer.parseInt(s);
                        }
                        else if(s.equals("0")){
                            //Log.d(TAG,"zero");
                            dataref.child(a.getUid()).child(Objects.requireNonNull(snap.getKey())).setValue(null);
                        }
                    }
                    test=sum+"";
                    //test = String.valueOf(snapshot.getChildrenCount());
                }
                //Toast.makeText(getApplicationContext(),a.getUid(),Toast.LENGTH_LONG).show();
                v.setText(test);
                v.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                v.setText(test);
                v.setVisibility(View.VISIBLE);
                //Toast.makeText(v.getContext(),"cartData Error",Toast.LENGTH_SHORT).show();
            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.cartMenu);
        item.setActionView(R.layout.cart_badge_action);

        TextView textview = item.getActionView().findViewById(R.id.actionbar_notifcation_textview);
        textview.setVisibility(View.INVISIBLE);
        getCount(textview);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),cartActivity.class));
                //Snackbar.make(nav_draw, "Cart " + textview.getText(), Snackbar.LENGTH_LONG).show();
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cartMenu) {// User chose the "Settings" item, show the app settings UI...


        }
        return super.onOptionsItemSelected(item);
    }
    public static String currency(Integer amount){
        String a= String.format(new Locale("en", "BD"),"%,.2f", amount.doubleValue());
        return "Tk. " + a;
    }
    private void askPermission() {
        ActivityCompat.requestPermissions(
                dashboardActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                11
        );
    }

}