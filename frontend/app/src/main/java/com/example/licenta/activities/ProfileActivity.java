package com.example.licenta.activities;

import static com.example.licenta.activities.SignInActivity.IP_ADDRESS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.licenta.adapter.ListViewAdapter;
import com.example.licenta.apiservice.MyApiService;
import com.example.licenta.classes.Recipe;
import com.example.licenta.R;
import com.example.licenta.classes.Ingredient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class ProfileActivity extends AppCompatActivity  implements View.OnClickListener {
    Button buttonSignOut;
    Intent intent;
    Toolbar toolbar;
    NavigationView navigationView;
    private boolean isLoggedIn = true;
    List<Ingredient> expIngredients = new ArrayList<>();
    private List<Recipe> recipes = new ArrayList<>();
    private Button breakfastButton, lunchButton, dinnerButton;
    private TextView emptyView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Recipe> searchRecipes;
    private Handler handler = new Handler();
    private Runnable runnableCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Long userId = Long.valueOf(sharedPreferences.getInt("userId", 0));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setTitle("Home");
        intent = getIntent();
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.navigationView);
        setupNavigationMenu();

        FirebaseMessaging.getInstance().subscribeToTopic("expiringIngredients")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("Firebase", "Subscribed to topic: expiringIngredients");
                    } else {
                        Log.e("Firebase", "Failed to subscribe to topic: expiringIngredients", task.getException());
                    }
                });


        progressBar = findViewById(R.id.progressbar2);
        emptyView = findViewById(R.id.empty_view2);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        getRandomRecipes();

        breakfastButton = findViewById(R.id.home_breakfast_filter);
        lunchButton = findViewById(R.id.home_lunch_filter);
        dinnerButton = findViewById(R.id.home_dinner_filter);

        breakfastButton.setOnClickListener(this);
        lunchButton.setOnClickListener(this);
        dinnerButton.setOnClickListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        runnableCode = new Runnable() {
            @Override
            public void run() {
                getExpiringIngredients(userId);
                handler.postDelayed(this, 1000 * 60 * 4);
            }
        };
        handler.post(runnableCode);
    }

    private void searchRecipe(String search) {
        searchRecipes = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);
        Call<JsonObject> call = apiService.searchRecipes(search, 30, true, "a962be76e36f47608777cf272923a70b");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonObject jsonObject = response.body();
                        JsonArray recipesArray = jsonObject.getAsJsonArray("results");
                        for (int i = 0; i < recipesArray.size(); i++) {
                            JsonObject recipeObject = recipesArray.get(i).getAsJsonObject();
                            String id = recipeObject.get("id").getAsString();
                            String title = recipeObject.get("title").getAsString();
                            JsonElement imageElement = recipeObject.get("image");
                            String image;

                            if (imageElement != null && !imageElement.isJsonNull()) {
                                image = "https://spoonacular.com/recipeImages/" + imageElement.getAsString();
                            } else {
                                image = "https://www.google.com/imgres?imgurl=https%3A%2F%2Flookaside.fbsbx.com%2Flookaside%2Fcrawler%2Fmedia%2F%3Fmedia_id%3D489329806525818&tbnid=gxe2hOwoCz7ThM&vet=12ahUKEwjcxoTG3uD_AhVDi_0HHRc8AWYQMygFegUIARDUAQ..i&imgrefurl=https%3A%2F%2Fwww.facebook.com%2Fp%2FNot-available-100063464086158%2F&docid=CLqTOE3cd8GgnM&w=566&h=492&q=not%20available&ved=2ahUKEwjcxoTG3uD_AhVDi_0HHRc8AWYQMygFegUIARDUAQ";
                            }

                            int servings = recipeObject.get("servings").getAsInt();
                            int readyInMinutes = recipeObject.get("readyInMinutes").getAsInt();
                            searchRecipes.add(new Recipe(id, title, image, servings, readyInMinutes));
                        }

                        progressBar.setVisibility(View.GONE);
                        if (searchRecipes.isEmpty()) {
                            recyclerView.setAlpha(0);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                            ListViewAdapter recyclerViewAdapter = new ListViewAdapter(getApplicationContext(), searchRecipes);
                            recyclerView.setAdapter(recyclerViewAdapter);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAlpha(1);
                        }
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("the response is error:", response.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("the call is failed:", t.toString());
            }
        });
    }

    public void getRandomRecipes() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyApiService apiService = retrofit.create(MyApiService.class);

        apiService.getRandomRecipes(30, true, "a962be76e36f47608777cf272923a70b").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();
                    JsonArray recipesArray = jsonResponse.getAsJsonArray("recipes");
                    for (JsonElement recipeElement : recipesArray) {
                        JsonObject recipeObject = recipeElement.getAsJsonObject();
                        String id = recipeObject.get("id").getAsString();
                        String title = recipeObject.get("title").getAsString();
                        String image = null;
                        if (recipeObject.has("image")) {
                          image = recipeObject.get("image").getAsString();
                        } else {
                            image = "https://www.google.com/imgres?imgurl=https%3A%2F%2Flookaside.fbsbx.com%2Flookaside%2Fcrawler%2Fmedia%2F%3Fmedia_id%3D489329806525818&tbnid=gxe2hOwoCz7ThM&vet=12ahUKEwjcxoTG3uD_AhVDi_0HHRc8AWYQMygFegUIARDUAQ..i&imgrefurl=https%3A%2F%2Fwww.facebook.com%2Fp%2FNot-available-100063464086158%2F&docid=CLqTOE3cd8GgnM&w=566&h=492&q=not%20available&ved=2ahUKEwjcxoTG3uD_AhVDi_0HHRc8AWYQMygFegUIARDUAQ";
                        }

                        int servings = recipeObject.get("servings").getAsInt();
                        int readyInMinutes = recipeObject.get("readyInMinutes").getAsInt();
                        recipes.add(new Recipe(id, title, image, servings, readyInMinutes));
                    }
                    progressBar.setVisibility(View.GONE);
                    ListViewAdapter myAdapter = new ListViewAdapter(getApplicationContext(), recipes);
                    recyclerView.setAdapter(myAdapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    Log.e("Response recipe", recipes.toString());
                } else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setAlpha(0);
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("the res is error:", t.toString());
                progressBar.setVisibility(View.GONE);
                recyclerView.setAlpha(0);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == breakfastButton) {
            searchRecipe("breakfast");
        } else if (v == lunchButton) {
            searchRecipe("lunch");

        } else if (v == dinnerButton) {
            searchRecipe("dinner");
        }
    }

    private void setupNavigationMenu() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_storage) {
                    Intent intent = new Intent(ProfileActivity.this, StorageActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (id == R.id.nav_signOut) {
                    logoutUser();
                    return true;
                }
                if (id == R.id.nav_history) {
                    Intent goToHome = new Intent(ProfileActivity.this, RecipeHistoryActivity.class);
                    startActivity(goToHome);
                    return true;
                }
                if (id == R.id.nav_saved) {
                    Intent intent = new Intent(ProfileActivity.this, SavedRecipeActivity.class);
                    startActivity(intent);
                    return true;
                }
                return true;
            }
        });
    }

    private void getExpiringIngredients(Long userId) {
        if (!isLoggedIn) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+ IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);
        Call<List<Ingredient>> call = apiService.getExpiringIngredients(userId);

        call.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                if (response.isSuccessful()) {
                    expIngredients.clear();
                    List<Ingredient> ingredients = response.body();
                    expIngredients.clear();
                    expIngredients.addAll(ingredients);

                    Log.e("expI", expIngredients.toString());

                    if (!expIngredients.isEmpty()) {
                        Log.e("exp", expIngredients.toString());
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Error retrieving expiring ingredients", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                Log.e("Error", "Error retrieving expiring ingredients", t);

            }
        });
    }

    private void logoutUser() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        Call<ResponseBody> call = apiService.logout();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent goToSignIn = new Intent(ProfileActivity.this, SignInActivity.class);
                    startActivity(goToSignIn);
                    finish();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(ProfileActivity.this, "Unauthorized. Please login again.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Logout failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Logout", t.toString());
            }
        });

        isLoggedIn = false;
    }
}