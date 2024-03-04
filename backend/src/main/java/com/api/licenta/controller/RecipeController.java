package com.api.licenta.controller;

import com.api.licenta.model.Recipe;
import com.api.licenta.model.RecipeIngredient;
import com.api.licenta.model.User;
import com.api.licenta.repository.RecipeIngredientRepository;
import com.api.licenta.service.impl.RecipeServiceImpl;
import com.api.licenta.service.interfaces.RecipeIngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RecipeController {
    @Autowired
    private RecipeServiceImpl recipeService;
    @Autowired
    private RecipeIngredientService recipeIngredientService;
    @RequestMapping("/recipes")
    @GetMapping()
    public List<Recipe> getAllRecipe() {
        return recipeService.findAll();
    }

    @GetMapping("recipes/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Optional<Recipe> recipe = recipeService.findById(id);
        if (recipe.isPresent()) {
            return new ResponseEntity<>(recipe.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/recipes/users/{userId}")
    public ResponseEntity<List<Recipe>> getRecipesByUserId(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        List<Recipe> recipes = recipeService.findAllByUser(user);
        if (!recipes.isEmpty()) {
            return new ResponseEntity<>(recipes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/recipes")
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        Recipe savedRecipe = recipeService.save(recipe);
        return new ResponseEntity<>(savedRecipe, HttpStatus.CREATED);
    }

    @DeleteMapping("recipes/{id}")
    public void delete(@PathVariable Long id) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);

        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            List<RecipeIngredient> recipeIngredients = recipeIngredientService.findAllByRecipe(recipe);
            for (RecipeIngredient recipeIngredient : recipeIngredients) {
                recipeIngredientService.delete(recipeIngredient.getRecipeIngredientId());
            }
            recipeService.delete(id);
        }
    }

}
