package com.api.licenta.service.impl;

import com.api.licenta.model.User;
import com.api.licenta.repository.UserRepository;
import com.api.licenta.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public String findPasswordByEmail(String email) {
        return userRepository.findPasswordByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
