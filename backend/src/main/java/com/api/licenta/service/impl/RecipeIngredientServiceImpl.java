package com.api.licenta.service.impl;

import com.api.licenta.model.Recipe;
import com.api.licenta.model.RecipeIngredient;
import com.api.licenta.repository.RecipeIngredientRepository;
import com.api.licenta.service.interfaces.RecipeIngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeIngredientServiceImpl implements RecipeIngredientService {

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Override
    public List<RecipeIngredient> findAll() {
        return recipeIngredientRepository.findAll();
    }

    @Override
    public Optional<RecipeIngredient> findById(Long id) {
        return recipeIngredientRepository.findById(id);
    }

    @Override
    public List<RecipeIngredient> findAllByRecipe(Recipe recipe) {
        return recipeIngredientRepository.findAllByRecipe(recipe);
    }

    @Override
    public RecipeIngredient save(RecipeIngredient recipeIngredient) {
        return recipeIngredientRepository.save(recipeIngredient);
    }

    @Override
    public void delete(Long id) {
        recipeIngredientRepository.deleteById(id);
    }
}
