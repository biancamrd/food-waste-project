package com.example.licenta.apiservice;

import com.example.licenta.classes.Brand;
import com.example.licenta.classes.Ingredient;
import com.example.licenta.classes.Recipe;
import com.example.licenta.classes.RecipeHistory;
import com.example.licenta.classes.RecipeIngredient;
import com.example.licenta.classes.TokenRequest;
import com.example.licenta.classes.User;
import com.example.licenta.helpers.RecipesResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyApiService {
    @Headers("Content-Type: application/json")
    @POST("login")
    Call<User> signIn(@Body RequestBody requestBody);

    @Headers("Content-Type: application/json")
    @POST("recipe_history")
    Call<RecipeHistory> postRecipeHistory(@Body RecipeHistory recipeHistory);

    @POST("recipe_ingredients/{savedRecipeId}")
    Call<JsonObject> saveRecipeIngredient(@Path("savedRecipeId") Long savedRecipeId, @Body RequestBody  requestBody);

    @Headers("Content-Type: application/json")
    @GET("recipe_history/users/{userId}")
    Call<List<RecipeHistory>> getRecipeHistoryByUserId(@Path("userId") Long userId);

    @Headers("Content-Type: application/json")
    @GET("recipes/random")
    Call<JsonObject> getRandomRecipes(@Query("number") int number, @Query("instructionsRequired") boolean instructionsRequired, @Query("apiKey") String apiKey);

    @Headers("Content-Type: application/json")
    @GET("recipes/findByIngredients")
    Call<JsonArray> findByIngredients(@Query("ingredients") String ingredients, @Query("number") int number, @Query("instructionsRequired") boolean instructionsRequired, @Query("apiKey") String apiKey);

    @Headers("Content-Type: application/json")
    @POST("register")
    Call<Void> registerUser(@Body RequestBody requestBody);

    @Headers("Content-Type: application/json")
    @GET("users/{userId}/expiringIngredients")
    Call<List<Ingredient>> getExpiringIngredients(@Path("userId") Long userId);

    @GET("recipes/search")
    Call<JsonObject> searchRecipes(@Query("query") String query, @Query("number") int number, @Query("instructionsRequired") boolean instructionsRequired, @Query("apiKey") String apiKey);

    @POST("recipes")
    Call<JsonObject> saveRecipe(@Body JsonObject recipeJson);

    @GET("recipes/{recipeId}/information")
    Call<JsonObject> getRecipeData(@Path("recipeId") String recipeId, @Query("apiKey") String apiKey, @Query("measures") String measures);

    @GET("users/{userId}/ingredients")
    Call<List<Ingredient>> getUserIngredients(@Path("userId") int userId);

    @GET("recipes/users/{userId}")
    Call<JsonArray> getRecipesByUserId(@Path("userId") Long userId);

    @GET("recipes/{recipeId}")
    Call<JsonObject> getRecipeData(@Path("recipeId") String recipeId);

    @GET("recipe_ingredients/recipes/{recipeId}")
    Call<JsonArray> getRecipeIngredientsByRecipeId(@Path("recipeId") Long recipeId);

    @POST("brands")
    Call<Void> createBrand(@Body Brand brand);

    @GET("brands/{name}")
    Call<JsonObject> getBrandByName(@Path("name") String name);

    @GET("products/brands/{brandId}")
    Call<JsonArray> getProductsByBrandId(@Path("brandId") Long brandId);

    @DELETE("ingredient/{ingredientId}")
    Call<Void> deleteIngredient(@Path("ingredientId") Long ingredientId);

    @POST("ingredients")
    Call<JsonObject> addIngredient(@Body JsonObject ingredientJson);

    @PUT("ingredient/{id}")
    Call<JsonObject> updateIngredient(@Path("id") Long id, @Body RequestBody requestBody);

    @POST("/logout")
    Call<ResponseBody> logout();

    @Headers("Content-Type: application/json")
    @POST("send-token")
    Call<ResponseBody> sendToken(@Body TokenRequest tokenRequest);

    @DELETE("recipes/{id}")
    Call<Void> deleteRecipe(@Path("id") String id);

    @POST("/check-expiring-ingredients")
    Call<Void> checkExpiringIngredients(@Path("userId") Long userId);

    @POST("/products")
    Call<JsonObject> createProduct(@Body JsonObject body);
}
