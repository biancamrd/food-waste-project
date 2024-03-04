package com.api.licenta.service.impl;

import com.api.licenta.model.Ingredient;
import com.api.licenta.model.User;
import com.api.licenta.service.interfaces.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientExpiryCheckService {

    private final UserServiceImpl userService;
    private final IngredientServiceImpl ingredientService;
    private final NotificationService notificationService;
    private Long userId;
    public IngredientExpiryCheckService(UserServiceImpl userService, IngredientServiceImpl ingredientService, NotificationService notificationService) {
        this.userService = userService;
        this.ingredientService = ingredientService;
        this.notificationService = notificationService;
    }

}