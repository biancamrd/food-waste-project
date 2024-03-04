package com.api.licenta.controller;

import com.api.licenta.model.Login;
import com.api.licenta.model.User;
import com.api.licenta.service.interfaces.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Login login, HttpServletResponse response) {
        String email = login.getEmail();
        String password = login.getPassword();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email cannot be empty");
        }

        Optional<User> user = userService.findByEmail(email);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email does not exist");
        }

        String hashedPassword = userService.findPasswordByEmail(email);

        if (!BCrypt.checkpw(password, hashedPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect email or password");
        }

        Cookie cookie = new Cookie("email", email);
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        return ResponseEntity.ok(user.get());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("email")) {
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}
