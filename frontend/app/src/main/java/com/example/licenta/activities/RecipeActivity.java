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

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.licenta.R;
import com.example.licenta.apiservice.MyApiService;
import com.example.licenta.classes.Ingredient;
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
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeActivity extends AppCompatActivity {

    private Button prepareRecipe;
    private TextView title, ready_in, servings, healthy, instructions, ingredients;
    private ImageView img, vegeterian;
    private List<Ingredient> ingredientsLst = new ArrayList<Ingredient>();
    private FloatingActionButton fab;
    private boolean like = false;
    List<Ingredient> recipeIngredients = new ArrayList<>();
    List<Ingredient> userIngredients = new ArrayList<>();
    Recipe recipe= new Recipe();
    Long userId;
    private Long savedRecipeId;
    String recipeId;
    List<RecipeIngredient> savedRecipeIngredients = new ArrayList<>();
    private List<String> recipeNames = new ArrayList<>();
    private List<Long> recipeIds = new ArrayList<>();
    private Long recipeToDeleteId = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        final Intent intent = getIntent();
        recipeId = Objects.requireNonNull(intent.getExtras()).getString("id");
        img = findViewById(R.id.recipe_img);
        title = findViewById(R.id.recipe_title);
        ready_in = findViewById(R.id.recipe_ready_in);
        servings = findViewById(R.id.recipe_servings);
        healthy = findViewById(R.id.recipe_healthy);
        vegeterian = findViewById(R.id.recipe_vegetarian);
        instructions = findViewById(R.id.recipe_instructions);
        ingredients = findViewById(R.id.textView_ingredients);
        fab = findViewById(R.id.floatingActionButton);
        prepareRecipe = findViewById(R.id.prepare_recipe_button);


        getRecipeData(recipeId);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String json = sharedPreferences.getString("USER_INGREDIENTS", null);
        SharedPreferences sharedPreferences2 = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = Long.valueOf(sharedPreferences2.getInt("userId", 0));
        Log.d("SearchResultsActivity", "JSON string: " + json);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Ingredient>>() {}.getType();
            userIngredients = gson.fromJson(json, type);
            Log.d("SearchResultsActivity", "userIngredients size: " + userIngredients.size());
        }
        getRecipesByUserId(userId);
        Log.e("recipe_name", recipeNames.toString());
        prepareRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRecipeHistory();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            boolean isFav = false;

            @Override
            public void onClick(View view) {
                if (isFav) {
                    deleteRecipe(String.valueOf(recipeToDeleteId));
                } else {
                    saveRecipe(recipe);
                }

                Log.e("ingr", savedRecipeIngredients.toString());
                isFav = !isFav;

                if (isFav) {
                    fab.setColorFilter(Color.RED);
                } else {
                    fab.setColorFilter(Color.BLACK);
                }
            }
        });


    }


    private void saveRecipe(Recipe recipe) {
        String URL = "http://" + IP_ADDRESS + ":8080/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService recipeService = retrofit.create(MyApiService.class);

        JsonObject userJson = new JsonObject();
        JsonObject recipeJson = new JsonObject();

        try {
            userJson.addProperty("id", userId);
            recipeJson.add("user", userJson);
            recipeJson.addProperty("image", recipe.getImage());
            recipeJson.addProperty("title", recipe.getTitle());
            recipeJson.addProperty("readyInMinutes", recipe.getReadyInMins());
            recipeJson.addProperty("servings", recipe.getAmountOfDishes());
            recipeJson.addProperty("veryHealthy", recipe.isHealthy());
            recipeJson.addProperty("vegetarian", recipe.isVegetarian());
            recipeJson.addProperty("instructions", recipe.getInstructions());
        } catch (JsonParseException e) {
            e.printStackTrace();
            return;
        }

        Call<JsonObject> call = recipeService.saveRecipe(recipeJson);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();
                    Log.i("the res is: ", jsonResponse.toString());
                    try {
                        savedRecipeId = jsonResponse.get("recipeId").getAsLong();
                        saveRecipeIngredients(savedRecipeIngredients, savedRecipeId);
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("the res is error: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("the call is failed: ", t.toString());
            }
        });
    }



    private void getRecipeData(String recipeId) {
        String URL = "https://api.spoonacular.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService recipeService = retrofit.create(MyApiService.class);

        Call<JsonObject> call = recipeService.getRecipeData(recipeId, "a962be76e36f47608777cf272923a70b", "us");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();
                    String recipeName = jsonResponse.get("title").getAsString();
                    if (recipeNames.contains(recipeName)) {
                        fab.setColorFilter(Color.RED);
                        int position = recipeNames.indexOf(recipeName);
                        recipeToDeleteId = recipeIds.get(position);
                    } else {
                        fab.setColorFilter(Color.BLACK);
                    }

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

                    if (jsonResponse.has("instructions") && !jsonResponse.get("instructions").isJsonNull()) {
                        instructions.setText(Html.fromHtml(jsonResponse.get("instructions").getAsString()));
                        recipe.setInstructions(instructions.getText().toString());
                    } else {
                        instructions.setText("Instructions not available");
                    }

                    JsonArray ingredientsArr = jsonResponse.getAsJsonArray("extendedIngredients");
                    String ingredientsText = "";

                    for (JsonElement ingredientElement : ingredientsArr) {
                        JsonObject ingredientObject = ingredientElement.getAsJsonObject();
                        String ingredientName = ingredientObject.get("name").getAsString();
                        JsonObject measuresObject = ingredientObject.getAsJsonObject("measures");

                        String quantity = "";
                        String unit = "";

                        if (measuresObject != null && measuresObject.has("us")) {
                            JsonObject usObject = measuresObject.getAsJsonObject("us");
                            if (usObject != null) {
                                quantity = usObject.get("amount").getAsString();
                                unit = usObject.get("unitShort").getAsString();
                            }
                        }

                        RecipeIngredient recipeIngredient = new RecipeIngredient();
                        Ingredient ingredient = new Ingredient();
                        ingredient.setName(ingredientName);
                        recipeIngredient.setName(ingredientName);
                        ingredient.setQuantity(Float.parseFloat(quantity));
                        ingredient.setUnit(unit);
                        recipeIngredient.setQuantity(Float.parseFloat(quantity));
                        recipeIngredient.setUnit(unit);
                        recipeIngredients.add(ingredient);
                        ingredientsLst.add(ingredient);
                        savedRecipeIngredients.add(recipeIngredient);
                        ingredientsText += "- " + quantity + " " + unit + " " + ingredientName + "\n";

                        User user = new User();
                        user.setId(Math.toIntExact(userId));
                        recipe.setUserId(user);
                    }

                    ingredients.setText(ingredientsText);
                } else {
                    Log.i("the res is error:", response.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("the call is failed:", t.toString());
            }
        });
    }



    private void saveRecipeIngredients(List<RecipeIngredient> recipeIngredients, Long savedRecipeId) {
        String baseURL = "http://" + IP_ADDRESS + ":8080/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService recipeService = retrofit.create(MyApiService.class);

        for (RecipeIngredient ingredient : recipeIngredients) {
            RecipeIngredient recipeIngredient = new RecipeIngredient();
            Recipe recipe = new Recipe();
            recipe.setRecipeId(savedRecipeId);
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setName(ingredient.getName());
            recipeIngredient.setQuantity(ingredient.getQuantity());
            recipeIngredient.setUnit(ingredient.getUnit());

            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("recipe", new JSONObject().putOpt("id", savedRecipeId));
                requestBody.put("ingredientName", recipeIngredient.getName());
                requestBody.put("quantity", recipeIngredient.getQuantity());
                requestBody.put("unit", recipeIngredient.getUnit());

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());

                Call<JsonObject> call = recipeService.saveRecipeIngredient(savedRecipeId, body);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.i("Saved recipe ingredient", response.body().toString());
                        } else {
                            Log.e("Error saving ingredient", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("Error saving ingredient", t.getMessage());
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
            public void onResponse(Call<RecipeHistory> call, Response<RecipeHistory> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RecipeActivity.this, "Recipe history created successfully", Toast.LENGTH_SHORT).show();
                    Log.e("Failed to create recipe history", recipeId);
                } else {
                    Toast.makeText(RecipeActivity.this, "Failed to create recipe history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeHistory> call, Throwable t) {
                Toast.makeText(RecipeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Failed to create recipe history", t.getMessage());
            }
        });
    }

    private void getRecipesByUserId(Long userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP_ADDRESS + ":8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        Call<JsonArray> call = apiService.getRecipesByUserId(userId);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    JsonArray jsonArray = response.body();
                    try {
                        for (JsonElement element : jsonArray) {
                            JsonObject jsonObject = element.getAsJsonObject();

                            String recipeName = jsonObject.get("title").getAsString();
                            recipeNames.add(recipeName);

                            Long recipeId = jsonObject.get("recipeId").getAsLong();
                            recipeIds.add(recipeId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Error retrieving recipes", response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Error retrieving recipes", t.getMessage());
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
                    Toast.makeText(RecipeActivity.this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                    fab.setColorFilter(Color.BLACK);
                } else {
                    Toast.makeText(RecipeActivity.this, "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RecipeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Failed to delete recipe", t.getMessage());
            }
        });
    }
}