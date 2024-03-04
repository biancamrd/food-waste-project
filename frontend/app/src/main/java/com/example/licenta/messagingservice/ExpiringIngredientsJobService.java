package com.example.licenta.messagingservice;

import static com.example.licenta.activities.SignInActivity.IP_ADDRESS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.licenta.classes.Ingredient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExpiringIngredientsJobService extends JobService {
    private RequestQueue requestQueue;

    @Override
    public boolean onStartJob(JobParameters params) {
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().subscribeToTopic("expiringIngredients")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseMessagingJob", "Subscribed to topic: expiringIngredients");
                        } else {
                            Log.d("FirebaseMessagingJob", "Failed to subscribe to topic: expiringIngredients");
                        }
                    }
                });
        requestQueue = Volley.newRequestQueue(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Long userId = Long.valueOf(sharedPreferences.getInt("userId", 0));
        getExpiringIngredients(params, userId);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
        return true;
    }

    private void getExpiringIngredients(JobParameters params, Long userId) {
        String url = "http://" + IP_ADDRESS + ":8080/users/" + userId + "/expiringIngredients";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        List<Ingredient> expIngredients = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Ingredient ingredient = new Ingredient();
                                ingredient.setName(jsonObject.getString("name"));
                                expIngredients.add(ingredient);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (!expIngredients.isEmpty()) {
                            sendNotifications(expIngredients);
                        }

                        jobFinished(params, false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        jobFinished(params, true);
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void sendNotifications(List<Ingredient> ingredients) {
        createNotificationChannel();
        for (Ingredient ingredient : ingredients) {
            String message = "Ingredient " + ingredient.getName() + " is expiring soon!";
            NotificationService.sendNotification(this, "Ingredient Expiration", message);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Channel Name";
            String channelDescription = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channelId", channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
