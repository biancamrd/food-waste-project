package com.example.licenta.activities;


import static com.example.licenta.activities.SignInActivity.IP_ADDRESS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import com.example.licenta.R;
import com.example.licenta.apiservice.MyApiService;
import com.example.licenta.classes.Recipe;
import com.example.licenta.adapter.RecyclerViewAdapterSearchResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

public class SavedRecipeActivity extends AppCompatActivity {
    Long userId;
    private RecyclerView recyclerView;
    List<Recipe> recipeList = new ArrayList<>();
    RecyclerViewAdapterSearchResult adapter;
    private ProgressBar progressBar;
    private JsonArray elements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipe);

        SharedPreferences sharedPreferences2 = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = (long) sharedPreferences2.getInt("userId", 0);
        progressBar = findViewById(R.id.progressbar3);

        recyclerView = findViewById(R.id.recycleview_ingredients_search_result);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RecyclerViewAdapterSearchResult(getApplicationContext(), recipeList, 2);
        recyclerView.setAdapter(adapter);
        loadRecipesFromAPI();
    }


    private void loadRecipesFromAPI() {
        String baseURL = "http://" + IP_ADDRESS + ":8080/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService recipeService = retrofit.create(MyApiService.class);

        Call<JsonArray> call = recipeService.getRecipesByUserId(userId);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    JsonArray jsonArray = response.body();
                    try {
                        elements = jsonArray;
                        recipeList.clear();
                        for (int i = 0; i < elements.size(); i++) {
                            JsonObject jsonObject = elements.get(i).getAsJsonObject();
                            Recipe recipe = new Recipe();
                            recipe.setRecipeId(jsonObject.get("recipeId").getAsLong());
                            recipe.setTitle(jsonObject.get("title").getAsString());
                            recipe.setThumbnail(jsonObject.get("image").getAsString());
                            recipeList.add(recipe);
                            Log.e("recipe", recipe.getRecipeId().toString());
                        }
                        progressBar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Error loading recipes", response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Error loading recipes", t.getMessage());
            }
        });
    }


}