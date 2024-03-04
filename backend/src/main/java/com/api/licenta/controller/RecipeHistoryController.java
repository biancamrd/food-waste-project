package com.api.licenta.controller;

import com.api.licenta.model.Recipe;
import com.api.licenta.model.RecipeHistory;
import com.api.licenta.model.User;
import com.api.licenta.service.interfaces.RecipeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RecipeHistoryController {
    @Autowired
    private RecipeHistoryService recipeHistoryService;

    @RequestMapping("/recipe_history")
    @GetMapping()
    public List<RecipeHistory> getAllRecipe() {
        return recipeHistoryService.findAll();
    }

    @GetMapping("recipe_history/{id}")
    public ResponseEntity<RecipeHistory> getRecipeHistoryById(@PathVariable Long id) {
        Optional<RecipeHistory> recipe = recipeHistoryService.findById(id);
        if (recipe.isPresent()) {
            return new ResponseEntity<>(recipe.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/recipe_history/users/{userId}")
    public ResponseEntity<List<RecipeHistory>> getRecipeHistoryByUserId(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        List<RecipeHistory> recipes = recipeHistoryService.findAllByUser(user);
        if (!recipes.isEmpty()) {
            return new ResponseEntity<>(recipes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/recipe_history")
    public ResponseEntity<RecipeHistory> createRecipe(@RequestBody RecipeHistory recipe) {
        RecipeHistory savedRecipe = recipeHistoryService.save(recipe);
        return new ResponseEntity<>(savedRecipe, HttpStatus.CREATED);
    }

    @DeleteMapping("recipe_history/{id}")
    public void delete(@PathVariable Long id) {
        recipeHistoryService.delete(id);
    }
}
