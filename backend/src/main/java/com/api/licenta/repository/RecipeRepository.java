package com.api.licenta.repository;

import com.api.licenta.model.Ingredient;
import com.api.licenta.model.Recipe;
import com.api.licenta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository  extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByUser(User user);
}
