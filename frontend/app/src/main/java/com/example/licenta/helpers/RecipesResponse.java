package com.example.licenta.helpers;

import com.example.licenta.classes.Recipe;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipesResponse {
    @SerializedName("recipes")
    private List<Recipe> recipes;

    public List<Recipe> getRecipes() {
        return recipes;
    }
}
