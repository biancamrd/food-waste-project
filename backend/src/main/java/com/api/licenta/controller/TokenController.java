package com.api.licenta.controller;


import com.api.licenta.model.TokenRequest;
import com.api.licenta.service.impl.IngredientExpiryCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TokenController {

    private IngredientExpiryCheckService ingredientExpiryCheckService;

    public TokenController(IngredientExpiryCheckService ingredientExpiryCheckService) {
        this.ingredientExpiryCheckService = ingredientExpiryCheckService;
    }

    @PostMapping("/send-token")
    public ResponseEntity<String> receiveToken(@PathVariable Long userId, @RequestBody TokenRequest tokenRequest) {
        String token = tokenRequest.getToken();
        return ResponseEntity.ok("Token received successfully");
    }
}


