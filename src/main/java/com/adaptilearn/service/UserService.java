package com.adaptilearn.service;

import com.adaptilearn.dto.SignUpRequest;
import com.adaptilearn.model.User;
import com.adaptilearn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User createUser(SignUpRequest signUpRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setCreatedAt(LocalDateTime.now());
        // Assign role based on request (default ROLE_USER as student). Teachers will get ROLE_TEACHER.
        String requestedRole = signUpRequest.getRole();
        if (requestedRole != null) {
            String normalized = requestedRole.trim().toUpperCase();
            if ("TEACHER".equals(normalized) || "ROLE_TEACHER".equals(normalized)) {
                user.addRole("ROLE_TEACHER");
            }
        }
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User updateLearningStyle(String username, String learningStyle) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLearningStyle(learningStyle);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
    
    public User updateLastLogin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

