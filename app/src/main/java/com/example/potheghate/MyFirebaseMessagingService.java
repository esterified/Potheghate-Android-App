package com.example.potheghate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String CHANNEL_ID = "fcm_channel_id";
    private int notificationId ;
    public static final String TAG="checkout";

    @Override
    public void onNewToken(@NonNull String s)
    {
        super.onNewToken(s);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String refreshToken=task.getResult();
                if( FirebaseAuth.getInstance().getCurrentUser()!=null){
                    updateToken(refreshToken);
                }
            }
        });

    }
    private void updateToken(String refreshToken){
        FirebaseDatabase.getInstance().getReference("user_token").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(refreshToken);
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: ");
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            showNotificationdata(remoteMessage);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage);

        }
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
        Log.d(TAG, "onMessageSent: ");
    }

    private void showNotificationdata(@NonNull RemoteMessage remoteMessage){
        notificationId = new Random().nextInt(3000);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_search)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat.from(this).notify(notificationId,builder.build());
    }
    private void showNotification(@NonNull RemoteMessage remoteMessage){
        notificationId = new Random().nextInt(3000);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_search)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat.from(this).notify(notificationId,builder.build());
    }

}
