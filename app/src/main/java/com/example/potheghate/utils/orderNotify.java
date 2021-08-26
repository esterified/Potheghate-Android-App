package com.example.potheghate.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.potheghate.courierActivity_h2c_checkout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class orderNotify {
    private Context context;
    public static final String TAG="checkout";
    public static final String serverKey = "key=AAAAqcx2vXQ:APA91bFsfCm85NkSQgrMoUCITy_nfflCl6h78mdFF1TsqlNYQUUyT95Cy1dLF3GVFShwzUlb6A61XquCUm8_uddeFTp3XzsGZrGvvcK23ScPbLJ4DmZrYg-GEpOEIuNLrFbVX-3bFKWO";

    public orderNotify(Context context) {
        this.context = context;
    }

    public void sendFCMMessage(){
        // The topic name can be optionally prefixed with "/topics/".
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(context,"error",Toast.LENGTH_SHORT).show();
                        }
                        // Get new FCM registration token
                        String token = task.getResult();

                        try {
                            sendUpstream(token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onComplete: "+token);

                    }
                });
    }

    public void sendUpstream(@NonNull String token) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://fcm.googleapis.com/fcm/send";

        JSONObject d = new JSONObject();
        d.put("title", "Order");
        d.put("body", "Order placed successfully!");
        JSONObject notification_data = new JSONObject();
        notification_data.put("data", d);
        notification_data.put("to",token);

        JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: volley"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onError: volley"+error.getLocalizedMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", serverKey);
                return headers;
            }

        };

        queue.add(request);
    }
}
