package com.example.licenta.activities;

import static com.example.licenta.activities.SignInActivity.IP_ADDRESS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.licenta.R;
import com.example.licenta.adapter.RecipeHistoryAdapter;
import com.example.licenta.apiservice.MyApiService;
import com.example.licenta.classes.Ingredient;
import com.example.licenta.classes.Recipe;
import com.example.licenta.classes.RecipeHistory;
import com.example.licenta.classes.RecipeIngredient;
import com.example.licenta.classes.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeHistoryActivity extends AppCompatActivity {

    List<RecipeHistory> recipeHistoryList = new ArrayList<>();
    Integer userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_history);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        getRecipeHistoryByUserId(Long.valueOf(userId));
    }


    private void displayRecipeHistory(List<RecipeHistory> recipeHistoryList) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_recipe_history);
        RecipeHistoryAdapter adapter = new RecipeHistoryAdapter(recipeHistoryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new RecipeHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String apiId) {
                Intent intent = new Intent(RecipeHistoryActivity.this, RecipeActivity.class);
                intent.putExtra("id", apiId);
                startActivity(intent);
            }
        });
    }

    private void getRecipeHistoryByUserId(Long userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyApiService apiService = retrofit.create(MyApiService.class);

        Call<List<RecipeHistory>> call = apiService.getRecipeHistoryByUserId(userId);
        call.enqueue(new Callback<List<RecipeHistory>>() {
            @Override
            public void onResponse(Call<List<RecipeHistory>> call, Response<List<RecipeHistory>> response) {
                if (response.isSuccessful()) {
                  recipeHistoryList = response.body();
                    displayRecipeHistory(recipeHistoryList);
                } else {
                    Toast.makeText(RecipeHistoryActivity.this, "Failed to retrieve recipe history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RecipeHistory>> call, Throwable t) {
                Toast.makeText(RecipeHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}