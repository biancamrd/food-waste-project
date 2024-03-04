package com.api.licenta.service.impl;

import com.api.licenta.model.Recipe;
import com.api.licenta.model.User;
import com.api.licenta.repository.RecipeRepository;
import com.api.licenta.service.interfaces.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;
    @Override
    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    @Override
    public List<Recipe> findAllByUser(User user) {
        return recipeRepository.findAllByUser(user);
    }


    @Override
    public Optional<Recipe> findById(Long id) {
        return recipeRepository.findById(id);
    }

    @Override
    public Recipe save(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    public void delete(Long id) {
        recipeRepository.deleteById(id);
    }

}
