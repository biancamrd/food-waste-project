package com.example.licenta.activities;

import static com.example.licenta.activities.SignInActivity.IP_ADDRESS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.example.licenta.R;
import com.example.licenta.apiservice.MyApiService;
import com.example.licenta.helpers.StringHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private EditText etLastName;
    private EditText etFirstName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    Button buttonRegister;
    private static final String BASE_URL = "http://192.168.0.101:8080/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etLastName = findViewById(R.id.etLastName);
        etFirstName = findViewById(R.id.etFirstName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        setupRegisterButton();
    }

    private void registerUser() {
        if (!validateFirstName() || !validateLastName() || !validateEmail() || !validatePasswordAndConfirm()) {
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstname", etFirstName.getText().toString());
            jsonObject.put("lastname", etLastName.getText().toString());
            jsonObject.put("email", etEmail.getText().toString());
            jsonObject.put("password", etPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<Void> call = apiService.registerUser(requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    etFirstName.setText(null);
                    etLastName.setText(null);
                    etEmail.setText(null);
                    etPassword.setText(null);
                    etConfirmPassword.setText(null);
                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Unsuccessful", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Registration Unsuccessful", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupRegisterButton() {
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public boolean validateFirstName() {
        String firstName = etFirstName.getText().toString();
        if (firstName.isEmpty()) {
            etFirstName.setError("First name cannot be empty!");
            return false;
        } else {
            etFirstName.setError(null);
            return true;
        }
    }

    public boolean validateLastName() {
        String lastName = etLastName.getText().toString();
        if (lastName.isEmpty()) {
            etLastName.setError("Last name cannot be empty!");
            return false;
        } else {
            etLastName.setError(null);
            return true;
        }
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

    public boolean validatePasswordAndConfirm() {
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            etPassword.setError("Password cannot be empty!");
            return false;
        } else if (!password.equals(confirmPassword)) {
            etPassword.setError("Password do not match!");
            return false;
        } else if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirm field cannot be empty!");
            return false;
        } else {
            etPassword.setError(null);
            etConfirmPassword.setError(null);
            return true;
        }
    }

    public void goToHome(View view){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSigInAct(View view){
        Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}