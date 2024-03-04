package com.api.licenta.service.impl;

import com.api.licenta.model.Ingredient;
import com.api.licenta.model.User;
import com.api.licenta.repository.IngredientRepository;
import com.api.licenta.service.interfaces.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientServiceImpl implements IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    @Override
    public Optional<Ingredient> findById(Long id) {
        return ingredientRepository.findById(id);
    }

    @Override
    public List<Ingredient> findAllByUser(User user) {
        return ingredientRepository.findAllByUser(user);
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Override
    public Optional<Ingredient> findByIdAnAndUser(Long id, User user) {
        return ingredientRepository.findByIdAndUser(id, user);
    }

    @Override
    public Ingredient updateIngredient(Long id, Ingredient newIngredient) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new RuntimeException("Ingredient with id " + id + " not found"));
        ingredient.setName(newIngredient.getName());
        ingredient.setQuantity(newIngredient.getQuantity());
        ingredient.setUnit(newIngredient.getUnit());
        ingredient.setExpirationDate(newIngredient.getExpirationDate());
        return ingredientRepository.save(ingredient);
    }

    @Override
    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }

    @Override
    public List<Ingredient> findExpiringIngredients(User user, Date date) {
        return ingredientRepository.findExpiringIngredients(user, date);
    }
}
