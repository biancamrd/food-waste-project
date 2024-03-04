package com.example.licenta.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.licenta.R;
import com.example.licenta.apiservice.MyApiService;
import com.example.licenta.classes.User;
import com.example.licenta.helpers.CookieInterceptor;
import com.example.licenta.helpers.StringHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class SignInActivity extends AppCompatActivity {
    private Button btnGoHome;
    private Button buttonLogin;
    private EditText etEmail;
    private EditText etPassword;

    public static final String IP_ADDRESS = "192.168.0.102";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        btnGoHome = findViewById(R.id.btnGoHome);
        buttonLogin = findViewById(R.id.buttonLogin);
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId != -1) {
            Intent goToProfile = new Intent(SignInActivity.this, ProfileActivity.class);
            startActivity(goToProfile);
            finish();
            return;
        }

        setupLoginButton();
    }

    private void setupLoginButton() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });
    }


    public void authenticateUser() {
        if( !validateEmail() || !validatePassword()) {
            return;
        }

        makeSignInRequest();
    }



    public void makeSignInRequest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params.toString());

        Call<User> call = apiService.signIn(requestBody);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        CookieInterceptor cookieInterceptor = new CookieInterceptor(sharedPreferences);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(cookieInterceptor)
                .build();


        Retrofit cookieRetrofit = retrofit.newBuilder()
                .client(okHttpClient)
                .build();

        MyApiService cookieApiService = cookieRetrofit.create(MyApiService.class);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User userResponse = response.body();
                    Integer userId = userResponse.getId();
                    String lastName = userResponse.getLastName();
                    String firstName = userResponse.getFirstName();
                    String email = userResponse.getEmail();
                    String password = userResponse.getPassword();

                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("userId", userId);
                    editor.apply();

                    Intent goToProfile = new Intent(SignInActivity.this, ProfileActivity.class);
                    startActivity(goToProfile);
                    finish();

                } else {
                    Log.e("SignInActivity", "Login Failed");
                    Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("SignInActivity", t.getMessage());
                Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        });
    }


    public boolean validateEmail() {
        String email = etEmail.getText().toString();
        if (email.isEmpty()) {
            etEmail.setError("Email cannot be empty!");
            return false;
        } else if (!StringHelper.emailValidationPattern(email)) {
            etEmail.setError("Please enter a valid email!");
            return false;
        } else {
            etEmail.setError(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String password = etPassword.getText().toString();

        if (password.isEmpty()) {
            etPassword.setError("Password cannot be empty!");
            return false;
        } else {
            etPassword.setError(null);
            return true;
        }
    }

    public void goToSigUpAct(View view){
        Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToHome(View view){
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}