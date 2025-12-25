package com.example.codeforge.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.codeforge.dto.auth.AuthResponse;
import com.example.codeforge.dto.auth.LoginRequest;
import com.example.codeforge.dto.auth.RegisterRequest;
import com.example.codeforge.dto.auth.UserMeResponse;
import com.example.codeforge.service.AuthService;
import com.example.codeforge.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(
            @RequestBody RegisterRequest request) {  // ✅ FIXED import
        return ApiResponse.success(
                authService.register(request),
                "User registered successfully"
        );
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @RequestBody LoginRequest request) {  // ✅ FIXED import
        return ApiResponse.success(
                authService.login(request),
                "Login successful"
        );
    }

    @GetMapping("/me")
@PreAuthorize("isAuthenticated()")
public ApiResponse<UserMeResponse> me(Authentication authentication) {
    return ApiResponse.success(
            authService.getCurrentUser(authentication.getName()),
            "Current user fetched"
    );
}

}