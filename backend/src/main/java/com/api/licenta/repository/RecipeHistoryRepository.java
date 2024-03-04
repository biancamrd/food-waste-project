package com.api.licenta.repository;


import com.api.licenta.model.RecipeHistory;
import com.api.licenta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeHistoryRepository extends JpaRepository<RecipeHistory, Long> {
    List<RecipeHistory> findAllByUser(User user);
}
