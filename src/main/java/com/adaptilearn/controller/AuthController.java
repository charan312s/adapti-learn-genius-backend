package com.adaptilearn.controller;

import com.adaptilearn.dto.AuthResponse;
import com.adaptilearn.dto.SignInRequest;
import com.adaptilearn.dto.SignUpRequest;
import com.adaptilearn.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping({"/api/auth", "/auth"})
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        log.info("Received signup request: {}", signUpRequest);
        log.info("Signup request details - Username: {}, Email: {}, FirstName: {}, LastName: {}", 
                signUpRequest.getUsername(), signUpRequest.getEmail(), 
                signUpRequest.getFirstName(), signUpRequest.getLastName());
        
        AuthResponse response = authService.signUp(signUpRequest);
        
        log.info("Signup response: {}", response);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        log.info("Received signin request: {}", signInRequest);
        log.info("Signin request details - UsernameOrEmail: {}", signInRequest.getUsernameOrEmail());
        
        AuthResponse response = authService.signIn(signInRequest);
        
        log.info("Signin response: {}", response);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            boolean isValid = authService.validateToken(token);
            return ResponseEntity.ok(isValid);
        }
        return ResponseEntity.ok(false);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("Health check endpoint called");
        return ResponseEntity.ok("Auth service is running!");
    }
    
    @PostMapping("/test")
    public ResponseEntity<String> test(@RequestBody String testData) {
        log.info("Test endpoint called with data: {}", testData);
        return ResponseEntity.ok("Test endpoint received: " + testData);
    }
}

