package com.adaptilearn.service;

import com.adaptilearn.dto.AuthResponse;
import com.adaptilearn.dto.SignInRequest;
import com.adaptilearn.dto.SignUpRequest;
import com.adaptilearn.model.User;
import com.adaptilearn.security.JwtUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    public AuthResponse signUp(SignUpRequest signUpRequest) {
        log.info("Signing up user: {}", signUpRequest);
        try {
            User user = userService.createUser(signUpRequest);
            
            // Generate JWT token
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities(user.getRoles().toArray(new String[0]))
                    .build();
            
            String token = jwtUtils.generateToken(userDetails);
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // 24 hours
            
            AuthResponse resp = new AuthResponse(token, user.getUsername(), user.getEmail(), 
                    user.getFirstName(), user.getLastName(), user.getLearningStyle(), expiresAt);
            resp.setRoles(user.getRoles());
            return resp;
                    
        } catch (Exception e) {
            return new AuthResponse("Registration failed: " + e.getMessage(), false);
        }
    }
    
    public AuthResponse signIn(SignInRequest signInRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInRequest.getUsernameOrEmail(),
                            signInRequest.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtils.generateToken(userDetails);
            
            // Update last login
            User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
            if (user != null) {
                userService.updateLastLogin(user.getUsername());
            }
            
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // 24 hours

            AuthResponse resp = new AuthResponse(token, user.getUsername(), user.getEmail(),
                    user.getFirstName(), user.getLastName(), user.getLearningStyle(), expiresAt);
            if (user != null) {
                resp.setRoles(user.getRoles());
            }
            return resp;
                    
        } catch (Exception e) {
            return new AuthResponse("Authentication failed: " + e.getMessage(), false);
        }
    }
    
    public boolean validateToken(String token) {
        return jwtUtils.validateToken(token);
    }
    
    public String getUsernameFromToken(String token) {
        return jwtUtils.extractUsername(token);
    }
}

