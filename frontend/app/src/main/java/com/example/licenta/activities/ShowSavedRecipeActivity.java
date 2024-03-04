package com.example.licenta.activities;

import static com.example.licenta.activities.SignInActivity.IP_ADDRESS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.R;
import com.example.licenta.apiservice.MyApiService;
import com.example.licenta.classes.Recipe;
import com.example.licenta.classes.RecipeHistory;
import com.example.licenta.classes.RecipeIngredient;
import com.example.licenta.classes.User;
import com.example.licenta.helpers.CustomDateSerializer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Response;


public class ShowSavedRecipeActivity extends AppCompatActivity {

    private Long userId;
    private List<Recipe> recipeList = new ArrayList<>();
    private TextView title, ready_in, servings, healthy, instructions, ingredients;
    private ImageView img, vegeterian;
    private FloatingActionButton fab;
    private boolean like = false;
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();
    private Recipe recipe= new Recipe();
    private String recipeId;
    private Button prepareRecipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_recipe);
        final Intent intent = getIntent();
        SharedPreferences sharedPreferences2 = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = Long.valueOf(sharedPreferences2.getInt("userId", 0));
        img = findViewById(R.id.recipe_img);
        title = findViewById(R.id.recipe_title);
        ready_in = findViewById(R.id.recipe_ready_in);
        servings = findViewById(R.id.recipe_servings);
        healthy = findViewById(R.id.recipe_healthy);
        vegeterian = findViewById(R.id.recipe_vegetarian);
        instructions = findViewById(R.id.recipe_instructions);
        ingredients = findViewById(R.id.textView_ingredients);
        fab = findViewById(R.id.floatingActionButton);
        recipeId = String.valueOf(Objects.requireNonNull(intent.getExtras()).getLong("id"));
        prepareRecipe = findViewById(R.id.prepare_recipe_button);

        getRecipeData(recipeId);

        prepareRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRecipeHistory();
            }
        });
        fab.setColorFilter(Color.RED);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecipe(recipeId);
            }
        });

    }

    private void createRecipeHistory() {
        long convertedRecipeId = Long.parseLong(recipeId);
        RecipeHistory recipeHistory = new RecipeHistory(new User(Math.toIntExact(userId)), title.getText().toString(), new Date(), convertedRecipeId);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new CustomDateSerializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        Call<RecipeHistory> call = apiService.postRecipeHistory(recipeHistory);
        call.enqueue(new Callback<RecipeHistory>() {
            @Override
            public void onResponse(Call<RecipeHistory> call, retrofit2.Response<RecipeHistory> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShowSavedRecipeActivity.this, "Recipe history created successfully", Toast.LENGTH_SHORT).show();
                    Log.e("Failed to create recipe history", recipeId);
                } else {
                    Toast.makeText(ShowSavedRecipeActivity.this, "Failed to create recipe history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeHistory> call, Throwable t) {
                Toast.makeText(ShowSavedRecipeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Failed to create recipe history", t.getMessage());
            }
        });
    }

    private void getRecipeData(final String recipeId) {
        String baseURL = "http://" + IP_ADDRESS + ":8080/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService recipeService = retrofit.create(MyApiService.class);

        Call<JsonObject> call = recipeService.getRecipeData(recipeId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();
                    try {
                        try {
                            Picasso.get().load(jsonResponse.get("image").getAsString()).into(img);
                            recipe.setImage(jsonResponse.get("image").getAsString());
                        } catch (Exception e) {
                            img.setImageResource(R.drawable.nopicture);
                        }
                        title.setText(jsonResponse.get("title").getAsString());

                        ready_in.setText(Integer.toString(jsonResponse.get("readyInMinutes").getAsInt()));
                        servings.setText(Integer.toString(jsonResponse.get("servings").getAsInt()));
                        recipe.setTitle(title.getText().toString());
                        recipe.setReadyInMins(Integer.parseInt(ready_in.getText().toString()));
                        recipe.setAmountOfDishes(Integer.parseInt(servings.getText().toString()));

                        if (jsonResponse.get("veryHealthy").getAsBoolean()) {
                            healthy.setText("Healthy");
                            recipe.setHealthy(true);
                        }

                        if (jsonResponse.get("vegetarian").getAsBoolean()) {
                            vegeterian.setImageResource(R.drawable.vegeterian);
                            recipe.setVegetarian(true);
                        }

                        try {
                            String instructionsValue = jsonResponse.get("instructions").getAsString();
                            if (instructionsValue.isEmpty()) {
                                throw new Exception("No Instructions");
                            } else {
                                instructions.setText(Html.fromHtml(instructionsValue));
                                recipe.setInstructions(instructions.getText().toString());
                            }
                        } catch (Exception e) {
                            String msg = "Unfortunately, the recipe you were looking for was not found. To view the original recipe, click on the link below: " + "<a href=" + jsonResponse.get("spoonacularSourceUrl").getAsString() + ">" + jsonResponse.get("spoonacularSourceUrl").getAsString() + "</a>";
                            instructions.setMovementMethod(LinkMovementMethod.getInstance());
                            instructions.setText(Html.fromHtml(msg));
                        }

                        getRecipeIngredientsByRecipeId(Long.valueOf(recipeId));
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Error retrieving recipe", response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Error retrieving recipe", t.getMessage());
            }
        });
    }

    private void updateIngredientsText() {
        String ingredientsText = "";
        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            String ingredientName = recipeIngredient.getName();
            String unit = recipeIngredient.getUnit();
            Float quantity = recipeIngredient.getQuantity();
            ingredientsText += "- " + quantity + " " + unit + " " + ingredientName + "\n";
        }

        ingredients.setText(ingredientsText);
    }

    private void getRecipeIngredientsByRecipeId(Long recipeId) {
        String baseURL = "http://" + IP_ADDRESS + ":8080/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService recipeService = retrofit.create(MyApiService.class);

        Call<JsonArray> call = recipeService.getRecipeIngredientsByRecipeId(recipeId);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    JsonArray jsonArray = response.body();
                    try {
                        for (JsonElement element : jsonArray) {
                            JsonObject jsonObject = element.getAsJsonObject();

                            RecipeIngredient recipeIngredient = new RecipeIngredient();
                            recipeIngredient.setId(jsonObject.get("recipeIngredientId").getAsLong());
                            recipeIngredient.setName(jsonObject.get("ingredientName").getAsString());
                            recipeIngredient.setQuantity(jsonObject.get("quantity").getAsFloat());
                            recipeIngredient.setUnit(jsonObject.get("unit").getAsString());

                            recipeIngredients.add(recipeIngredient);
                        }
                        Log.e("getRecipeIngredientsByRecipeId", String.valueOf(recipeIngredients));
                        updateIngredientsText();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Error retrieving ingredients", response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Error retrieving ingredients", t.getMessage());
            }
        });
    }

    private void deleteRecipe(String recipeId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        Call<Void> call = apiService.deleteRecipe(recipeId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShowSavedRecipeActivity.this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                    fab.setColorFilter(Color.BLACK);
                } else {
                    Toast.makeText(ShowSavedRecipeActivity.this, "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShowSavedRecipeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Failed to delete recipe", t.getMessage());
            }
        });
    }

}