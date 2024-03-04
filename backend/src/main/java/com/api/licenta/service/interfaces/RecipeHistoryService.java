package com.api.licenta.service.interfaces;

import com.api.licenta.model.Recipe;
import com.api.licenta.model.RecipeHistory;
import com.api.licenta.model.User;

import java.util.List;
import java.util.Optional;

public interface RecipeHistoryService {
    List<RecipeHistory> findAll();

    List<RecipeHistory> findAllByUser(User user);

    Optional<RecipeHistory> findById(Long id);

    RecipeHistory save(RecipeHistory recipe);

    void delete(Long id);

}
