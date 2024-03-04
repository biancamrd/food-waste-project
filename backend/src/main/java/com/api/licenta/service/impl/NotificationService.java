package com.api.licenta.service.impl;

import com.api.licenta.model.Ingredient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    public void sendNotification(List<Ingredient> expiringIngredients) {
        for (Ingredient ingredient : expiringIngredients) {
            String messageText = "Ingredient " + ingredient.getName() + " is expiring SOON!";

            Message message = Message.builder()
                    .putData("title", "Ingredient Expiration")
                    .putData("message", messageText)
                    .setTopic("/topics/expiringIngredients")
                    .build();

            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Successfully sent message: " + response);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
