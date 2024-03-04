package com.api.licenta.service.interfaces;

import com.api.licenta.model.Ingredient;
import com.api.licenta.model.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface IngredientService {
    List<Ingredient> findAll();
    Optional<Ingredient> findById(Long id);

    List<Ingredient> findAllByUser(User user);

    Ingredient save(Ingredient ingredient);

    Optional<Ingredient> findByIdAnAndUser(Long id, User user);

    Ingredient updateIngredient(Long id, Ingredient newIngredient);

    void deleteIngredient(Long id);

    List<Ingredient> findExpiringIngredients(User user, Date date);


}
