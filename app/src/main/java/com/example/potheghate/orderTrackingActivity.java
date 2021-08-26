package com.example.potheghate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

public class orderTrackingActivity extends AppCompatActivity {
    public static final String TAG = "order";

    private int count=0;
    View dView;
    ProgressBar dProg;
    int mStepCount=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);
        //test(0);
        thread.start();


    }
    Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {
            View v1 = findViewById(R.id.view1);
            fillCircle(v1,1);
        }
    });

    private void fillCircle(View v,int step) {
        v.setAlpha(0);
        v.setBackgroundResource(R.drawable.flag_green_dark_order);
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                float f=(float)count/100;
                v.setAlpha(f);

                count += 10;
                if (count == 100) {
                    count=0;
                    this.cancel();

                    if (step+1 == 2 && mStepCount>=2) {
                        dProg = findViewById(R.id.view_line1);
                        fillLine(dProg, step+1);
                    }
                    else if(step+1 == 3 && mStepCount>=3){
                        dProg = findViewById(R.id.view_line2);
                        fillLine(dProg, step+1);
                    }
                }
                //Log.d(TAG, "run: ss"+step+":"+count);

            }
        };

        Timer ti = new Timer();
        ti.schedule(timerTask, 100, 10);

    }
    private void fillLine(ProgressBar p,int step) {

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                p.setProgress(count);
                count += 1;
                if (count == 100) {
                    count=0;
                    this.cancel();
                    if (step == 2) {
                        dView = findViewById(R.id.view2);
                        fillCircle(dView, step);

                    } else if (step == 3) {
                        dView = findViewById(R.id.view3);
                        fillCircle(dView, step+1);
                    }
                }

            }
        };
        Timer ti = new Timer();
        ti.schedule(tt, 100, 10);

    }
    public void buttonClick(View view) {
        startActivity(new Intent(getApplicationContext(), dashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
    public void test(int c){
        if(c<=20){
            test(c+1);
        }
        else {
        return;
        }
        Log.d(TAG, "test: "+c);

    }
}