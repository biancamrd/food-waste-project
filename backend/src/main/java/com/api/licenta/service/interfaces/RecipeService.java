package com.api.licenta.service.interfaces;

import com.api.licenta.model.Recipe;
import com.api.licenta.model.User;

import java.util.List;
import java.util.Optional;

public interface RecipeService {
    List<Recipe> findAll();

    List<Recipe> findAllByUser(User user);

    Optional<Recipe> findById(Long id);

    Recipe save(Recipe recipe);

    void delete(Long id);


}
