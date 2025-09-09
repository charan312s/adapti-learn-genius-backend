package com.adaptilearn.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String learningStyle;
    private java.util.Set<String> roles;
    private LocalDateTime expiresAt;
    private String message;
    private boolean success;
//    private Set<String> roles = new HashSet<>();
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String token, String username, String email, String firstName, String lastName, String learningStyle, LocalDateTime expiresAt) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.learningStyle = learningStyle;
        this.expiresAt = expiresAt;
        this.success = true;
    }
    
    public AuthResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getLearningStyle() {
        return learningStyle;
    }
    
    public void setLearningStyle(String learningStyle) {
        this.learningStyle = learningStyle;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {this.roles = roles;}
}

