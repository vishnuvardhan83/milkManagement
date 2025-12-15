package com.milkmanagement.controller;

import com.milkmanagement.dto.CustomerDTO;
import com.milkmanagement.dto.CustomerSignupDTO;
import com.milkmanagement.dto.JwtResponse;
import com.milkmanagement.dto.LoginRequest;
import com.milkmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody LoginRequest signUpRequest) {
        try {
            authService.registerUser(
                signUpRequest.getUsername(),
                signUpRequest.getUsername() + "@milk.com", // Simple email generation
                signUpRequest.getPassword(),
                null // Default role
            );
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/customer/signup")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerSignupDTO signupDTO) {
        try {
            CustomerDTO customerDTO = authService.registerCustomer(signupDTO);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                    .body(customerDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Utility endpoint to generate BCrypt password hash for SQL inserts
     * POST /api/auth/hash-password
     * Body: { "password": "your_password_here" }
     */
    @PostMapping("/hash-password")
    public ResponseEntity<?> hashPassword(@RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Error: Password is required");
            }
            
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hash = encoder.encode(password);
            
            Map<String, String> response = new HashMap<>();
            response.put("password", password);
            response.put("hash", hash);
            response.put("sqlExample", 
                "INSERT INTO users (username, email, password, enabled) " +
                "VALUES ('your_username', 'your_email@example.com', '" + hash + "', TRUE);");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
