package com.api.licenta.service.interfaces;

import com.api.licenta.model.Ingredient;
import com.api.licenta.model.Recipe;
import com.api.licenta.model.RecipeIngredient;
import com.api.licenta.model.User;

import java.util.List;
import java.util.Optional;

public interface RecipeIngredientService {
    List<RecipeIngredient> findAll();

    Optional<RecipeIngredient> findById(Long id);

    List<RecipeIngredient> findAllByRecipe(Recipe recipe);

    RecipeIngredient save(RecipeIngredient recipeIngredient);

    void delete(Long id);
}
