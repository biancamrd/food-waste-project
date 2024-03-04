package com.api.licenta.repository;

import com.api.licenta.model.Recipe;
import com.api.licenta.model.RecipeIngredient;
import com.api.licenta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeIngredientRepository  extends JpaRepository<RecipeIngredient, Long> {
    List<RecipeIngredient> findAllByRecipe(Recipe recipe);
}
