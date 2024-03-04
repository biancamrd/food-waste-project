package com.api.licenta;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
public class LicentaApplication {

    public static void main(String[] args) {

        SpringApplication.run(LicentaApplication.class, args);

        FileInputStream serviceAccount = null;
        FirebaseOptions options = null;

        try {
            serviceAccount = new FileInputStream(LicentaApplication.class.getResource("/my-final-app-ebbe9-firebase-adminsdk-qpqzr-88e409a9a7.json").getPath());
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FirebaseApp.initializeApp(options);

    }
}
