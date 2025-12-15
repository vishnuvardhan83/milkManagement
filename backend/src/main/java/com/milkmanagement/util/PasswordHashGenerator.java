package com.milkmanagement.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes
 * Run this main method to generate password hashes for SQL inserts
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hash for admin123
        String password = "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("==========================================");
        System.out.println("Password Hash Generator");
        System.out.println("==========================================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("==========================================");
        System.out.println("\nSQL INSERT statement:");
        System.out.println("INSERT INTO users (username, email, password, enabled)");
        System.out.println("VALUES ('your_username', 'your_email@example.com', '" + hash + "', TRUE);");
        System.out.println("==========================================");
        
        // Verify the hash
        boolean matches = encoder.matches(password, hash);
        System.out.println("\nVerification: " + (matches ? "✓ Hash is valid" : "✗ Hash is invalid"));
    }
    
    /**
     * Generate hash for a given password
     * @param password Plain text password
     * @return BCrypt hashed password
     */
    public static String generateHash(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
