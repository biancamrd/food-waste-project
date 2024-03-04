package com.api.licenta.controller;

import com.api.licenta.model.Ingredient;
import com.api.licenta.model.User;
import com.api.licenta.repository.IngredientRepository;
import com.api.licenta.service.impl.IngredientServiceImpl;
import com.api.licenta.service.impl.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class IngredientController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IngredientServiceImpl ingredientService;
    @RequestMapping("/ingredients")
    @GetMapping()
    public List<Ingredient> getAllIngredients() {
        return ingredientService.findAll();
    }

    @GetMapping("ingredients/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        Optional<Ingredient> ingredient = ingredientService.findById(id);
        if (ingredient.isPresent()) {
            return new ResponseEntity<>(ingredient.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/{userId}/ingredients")
    public ResponseEntity<List<Ingredient>> getIngredientsByUserId(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        List<Ingredient> ingredients = ingredientService.findAllByUser(user);
        if (!ingredients.isEmpty()) {
            return new ResponseEntity<>(ingredients, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/ingredients")
    public ResponseEntity<Ingredient> createIngredient(@RequestBody Ingredient ingredient) {
        Ingredient savedIngredient = ingredientService.save(ingredient);
        return new ResponseEntity<>(savedIngredient, HttpStatus.CREATED);
    }

    @PutMapping("/ingredient/{id}")
    public Ingredient update(@RequestBody Ingredient ingredient, @PathVariable Long id) {
       return ingredientService.updateIngredient(id, ingredient);
    }

    @DeleteMapping("ingredient/{id}")
    public void delete(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
    }

    @GetMapping("/users/{userId}/expiringIngredients")
    public ResponseEntity<List<Ingredient>> getExpiringIngredients(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 3);
        Date threeDaysFromNow = c.getTime();

        List<Ingredient> expiringIngredients = ingredientService.findExpiringIngredients(user, threeDaysFromNow);

        if (!expiringIngredients.isEmpty()) {
            notificationService.sendNotification(expiringIngredients);
        }

        return ResponseEntity.ok(expiringIngredients);
    }
}
