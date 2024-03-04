package com.api.licenta.controller;


import com.api.licenta.model.Recipe;
import com.api.licenta.model.RecipeIngredient;
import com.api.licenta.service.impl.RecipeIngredientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RecipeIngredientController {
    @Autowired
    private RecipeIngredientServiceImpl recipeIngredientService;

    @RequestMapping("/recipe_ingredients")
    @GetMapping()
    public List<RecipeIngredient> getAllRecipeIngredients() {
        return recipeIngredientService.findAll();
    }

    @GetMapping("recipe_ingredients/{id}")
    public ResponseEntity<RecipeIngredient> getIngredientById(@PathVariable Long id) {
        Optional<RecipeIngredient> recipeIngredient = recipeIngredientService.findById(id);
        if (recipeIngredient.isPresent()) {
            return new ResponseEntity<>(recipeIngredient.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/recipe_ingredients/{recipeId}")
    public ResponseEntity<RecipeIngredient> createRecipeIngredient(@PathVariable Long recipeId, @RequestBody RecipeIngredient recipeIngredient) {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(recipeId);
        recipeIngredient.setRecipe(recipe);
        RecipeIngredient savedIngredient = recipeIngredientService.save(recipeIngredient);
        return new ResponseEntity<>(savedIngredient, HttpStatus.CREATED);
    }


    @GetMapping("/recipe_ingredients/recipes/{recipeId}")
    public ResponseEntity<List<RecipeIngredient>> getIngredientsByRecipeId(@PathVariable Long recipeId) {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(recipeId);
        List<RecipeIngredient> recipeIngredients = recipeIngredientService.findAllByRecipe(recipe);
        if (!recipeIngredients.isEmpty()) {
            return new ResponseEntity<>(recipeIngredients, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
