package com.api.licenta.controller;

import com.api.licenta.model.User;
import com.api.licenta.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping
    @RequestMapping(value = "/users")
    public List<User> getUsers() {
       return userService.getAllUsers();
    }

    @GetMapping
    @RequestMapping(value = "/users/{userId}")
    public Optional<User> getUser(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @PostMapping
    @RequestMapping(value = "/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        String first_name = user.getFirstname();
        String last_name = user.getLastname();
        String email = user.getEmail();
        String password = user.getPassword();

        if(first_name == null || first_name.isEmpty() || last_name == null || last_name.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()){
            return new ResponseEntity<>("Please Complete all Fields", HttpStatus.BAD_REQUEST);
        }

        String hashed_password = BCrypt.hashpw(password, BCrypt.gensalt());

        User newUser = new User();
        newUser.setFirstname(first_name);
        newUser.setLastname(last_name);
        newUser.setEmail(email);
        newUser.setPassword(hashed_password);
        User savedUser = userService.save(newUser);
        if(savedUser == null){
            return new ResponseEntity<>("failed", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
