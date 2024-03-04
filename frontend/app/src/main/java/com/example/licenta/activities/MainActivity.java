package com.example.licenta.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.licenta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    Button btnSignUp;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        btnSignUp = findViewById(R.id.btnSignup);
        btnSignIn = findViewById(R.id.btnSignin);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId != -1) {
            Intent goToProfile = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(goToProfile);
            finish();
            return;
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channelName";
            String description = "channelDescription";
            int importance =
                    NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channelId", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSignIn(View view) {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSignIn() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

}