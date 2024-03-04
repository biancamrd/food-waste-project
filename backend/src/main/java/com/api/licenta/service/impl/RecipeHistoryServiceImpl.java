package com.api.licenta.service.impl;

import com.api.licenta.model.RecipeHistory;
import com.api.licenta.model.User;
import com.api.licenta.repository.RecipeHistoryRepository;
import com.api.licenta.service.interfaces.RecipeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeHistoryServiceImpl implements RecipeHistoryService {
    @Autowired
    private RecipeHistoryRepository recipeHistoryRepository;


    @Override
    public List<RecipeHistory> findAll() {
        return recipeHistoryRepository.findAll();
    }

    @Override
    public List<RecipeHistory> findAllByUser(User user) {
        return recipeHistoryRepository.findAllByUser(user);
    }

    @Override
    public Optional<RecipeHistory> findById(Long id) {
        return recipeHistoryRepository.findById(id);
    }

    @Override
    public RecipeHistory save(RecipeHistory recipe) {
        return recipeHistoryRepository.save(recipe);
    }

    @Override
    public void delete(Long id) {
        recipeHistoryRepository.deleteById(id);
    }
}
