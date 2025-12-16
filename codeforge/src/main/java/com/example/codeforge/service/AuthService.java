package com.example.codeforge.service;

import com.example.codeforge.dto.AuthResponse;
import com.example.codeforge.dto.LoginRequest;
import com.example.codeforge.dto.RegisterRequest;
import com.example.codeforge.entity.User;
import com.example.codeforge.repository.UserRepository;
import com.example.codeforge.utils.JwtUtil;
import com.example.codeforge.utils.RoleConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    /**
     * User Registration
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with username: {}", request.getUsername());
        
        // Validate input
        validateRegisterRequest(request);
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username already exists - {}", request.getUsername());
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleConstants.USER)  // Default role is USER
                .build();
        
        // Save user to database
        userRepository.save(user);
        log.info("User registered successfully: {}", request.getUsername());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        
        // Return response
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .message("User registered successfully")
                .build();
    }
    
    /**
     * User Login
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to login user with username: {}", request.getUsername());
        
        // Validate input
        validateLoginRequest(request);
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            
            // Get user from database
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            
            log.info("User logged in successfully: {}", request.getUsername());
            
            // Return response
            return AuthResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .message("Login successful")
                    .build();
            
        } catch (BadCredentialsException e) {
            log.warn("Login failed: Invalid credentials for user - {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            throw new RuntimeException("Authentication failed");
        }
    }
    
    /**
     * Validate Register Request
     */
    private void validateRegisterRequest(RegisterRequest request) {
        log.debug("Validating register request");
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        
        if (request.getUsername().length() < 3) {
            throw new RuntimeException("Username must be at least 3 characters");
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Invalid email format");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        
        if (request.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
    }
    
    /**
     * Validate Login Request
     */
    private void validateLoginRequest(LoginRequest request) {
        log.debug("Validating login request");
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
    }
    
    /**
     * Get user info by username (for other services)
     */
    public User getUserByUsername(String username) {
        log.debug("Fetching user info for username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}