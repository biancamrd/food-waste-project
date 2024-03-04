package com.example.licenta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.licenta.R;
import com.example.licenta.apiservice.MyApiService;
import com.example.licenta.classes.Recipe;
import com.example.licenta.adapter.RecyclerViewAdapterSearchResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultsActivity extends AppCompatActivity {
    private TextView ingredients_list;
    private RecyclerView recyclerView;
    private JSONArray jsonArray;
    private List<Recipe> recipes = new ArrayList<>();
    private ProgressBar progressBar;
    private String searchText;


    private ArrayList<String> selectedIngredients;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ingredients_list= findViewById(R.id.ingredients_names_list);
        progressBar = findViewById(R.id.progressbar3);
        Intent intent = getIntent();
        selectedIngredients = intent.getStringArrayListExtra("selectedIngredients");
        StringBuilder sb = new StringBuilder();
        for (String ingredient : selectedIngredients) {
            sb.append(ingredient).append(", ");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            searchText = sb.toString();
        } else {
            searchText = "";
        }
        ingredients_list.setText(searchText);
        getRecipesByIngredients(searchText);
    }

    public ArrayList<String> getSelectedIngredients() {
        return selectedIngredients;
    }


    public void getRecipesByIngredients(String searchText) {
        recyclerView = findViewById(R.id.recycleview_ingredients_search_result);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        apiService.findByIngredients(searchText, 30, true,
                "a962be76e36f47608777cf272923a70b").enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    JsonArray jsonArray = response.body();
                    for (JsonElement recipeElement : jsonArray) {
                        JsonObject recipeObject = recipeElement.getAsJsonObject();
                        String id = recipeObject.get("id").getAsString();
                        String title = recipeObject.get("title").getAsString();
                        String image = recipeObject.get("image").getAsString();
                        recipes.add(new Recipe(id, title, image, 0, 0));
                    }
                    progressBar.setVisibility(View.GONE);
                    RecyclerViewAdapterSearchResult recyclerViewAdapterSearchResult =
                            new RecyclerViewAdapterSearchResult(getApplicationContext(), recipes, 1);
                    recyclerView.setAdapter(recyclerViewAdapterSearchResult);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("Error:", t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}