package com.example.licenta.messagingservice;

import static com.example.licenta.activities.SignInActivity.IP_ADDRESS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.licenta.R;
import com.example.licenta.apiservice.MyApiService;
import com.example.licenta.classes.TokenRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";
    private static AtomicInteger notificationId = new AtomicInteger(0);

    private SharedPreferences sharedPreferences;
    private int userId;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            showNotification(title, message);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);


        Log.e("Refreshed token: ", String.valueOf(userId));
        sendUserIdToServer((long) userId);
        sendTokenToServer(token);
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_icon)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId.incrementAndGet(), builder.build());    }

    private void sendTokenToServer(String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token);
            jsonObject.put("userId", userId);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(token);
        Call<ResponseBody> call = apiService.sendToken(tokenRequest);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                } else {
                    int statusCode = response.code();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void sendUserIdToServer(Long userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        Call<Void> call = apiService.checkExpiringIngredients(userId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                } else {

                    int statusCode = response.code();

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
