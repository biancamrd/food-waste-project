package com.api.licenta.repository;

import com.api.licenta.model.Ingredient;
import com.api.licenta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findAll();
    Optional<Ingredient> findById(Long id);

    List<Ingredient> findAllByUser(User user);

    Ingredient save(Ingredient ingredient);

    Optional<Ingredient> findByIdAndUser(Long id, User user);

    void deleteById(Long id);

    @Query("SELECT i FROM Ingredient i WHERE i.user = :user AND i.expirationDate <= :date")
    List<Ingredient> findExpiringIngredients(User user, Date date);
}
