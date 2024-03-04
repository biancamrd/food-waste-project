package com.api.licenta.service.interfaces;

import com.api.licenta.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User save(User user);

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    String findPasswordByEmail(String email);
}

