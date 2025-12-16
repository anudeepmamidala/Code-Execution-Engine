package com.example.codeforge.controller;

import com.example.codeforge.dto.AuthResponse;
import com.example.codeforge.dto.LoginRequest;
import com.example.codeforge.dto.RegisterRequest;
import com.example.codeforge.dto.SubmissionListResponse;
import com.example.codeforge.dto.SubmissionResponse;
import com.example.codeforge.service.AuthService;
import com.example.codeforge.service.AdminSubmissionService;
import com.example.codeforge.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication endpoints")
@Slf4j
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private AdminSubmissionService adminSubmissionService;
    
    /**
     * User Registration Endpoint
     * POST /api/auth/register
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("Register endpoint called for username: {}", request.getUsername());
        
        try {
            AuthResponse authResponse = authService.register(request);
            
            log.info("User registered successfully: {}", request.getUsername());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            authResponse,
                            "User registered successfully"
                    ));
        } catch (RuntimeException e) {
            log.error("Registration error: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during registration: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }
    
    /**
     * User Login Endpoint
     * POST /api/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        
        log.info("Login endpoint called for username: {}", request.getUsername());
        
        try {
            AuthResponse authResponse = authService.login(request);
            
            log.info("User logged in successfully: {}", request.getUsername());
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(
                            authResponse,
                            "Login successful"
                    ));
        } catch (RuntimeException e) {
            log.error("Login error: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }
    
    /**
     * Health Check Endpoint
     * GET /api/health
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Checks if auth service is running")
    public ResponseEntity<ApiResponse<String>> health() {
        log.info("Health check endpoint called");
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Auth service is running"));
    }
    
    // ============================================
    // SUBMISSION ENDPOINTS (ADMIN)
    // ============================================
    
    /**
     * Get all submissions (admin view)
     * GET /api/auth/submissions
     */
    @GetMapping("/submissions")
    @Operation(summary = "Get all submissions (Admin)", description = "View all user submissions")
    public ResponseEntity<ApiResponse<List<SubmissionListResponse>>> getAllSubmissions() {
        log.info("Admin: Fetching all submissions");
        
        try {
            List<SubmissionListResponse> submissions = adminSubmissionService.getAllSubmissions();
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(
                            submissions,
                            "All submissions retrieved successfully"
                    ));
        } catch (Exception e) {
            log.error("Admin: Error fetching submissions: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching submissions: " + e.getMessage()));
        }
    }
    
    /**
     * Get submission details (admin)
     * GET /api/auth/submissions/{id}
     */
    @GetMapping("/submissions/{id}")
    @Operation(summary = "Get submission details (Admin)", description = "View submission with all details")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionDetails(
            @PathVariable Long id) {
        log.info("Admin: Fetching submission {}", id);
        
        try {
            SubmissionResponse submission = adminSubmissionService.getSubmissionDetails(id);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(
                            submission,
                            "Submission retrieved successfully"
                    ));
        } catch (RuntimeException e) {
            log.warn("Admin: Submission not found: {}", id);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Admin: Error fetching submission: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching submission: " + e.getMessage()));
        }
    }
    
    /**
     * Get submissions by status
     * GET /api/auth/submissions/status/{status}
     */
    @GetMapping("/submissions/status/{status}")
    @Operation(summary = "Get submissions by status (Admin)", 
               description = "Filter submissions by status (PENDING, EXECUTING, COMPLETED, FAILED)")
    public ResponseEntity<ApiResponse<List<SubmissionListResponse>>> getSubmissionsByStatus(
            @PathVariable String status) {
        log.info("Admin: Fetching submissions by status: {}", status);
        
        try {
            List<SubmissionListResponse> submissions = adminSubmissionService.getSubmissionsByStatus(status);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(
                            submissions,
                            "Submissions retrieved successfully"
                    ));
        } catch (RuntimeException e) {
            log.warn("Admin: Invalid status: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Admin: Error fetching submissions: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching submissions: " + e.getMessage()));
        }
    }
    
    /**
     * Get submissions for a problem
     * GET /api/auth/problems/{problemId}/submissions
     */
    @GetMapping("/problems/{problemId}/submissions")
    @Operation(summary = "Get problem submissions (Admin)", 
               description = "View all submissions for a specific problem")
    public ResponseEntity<ApiResponse<List<SubmissionListResponse>>> getSubmissionsForProblem(
            @PathVariable Long problemId) {
        log.info("Admin: Fetching submissions for problem {}", problemId);
        
        try {
            List<SubmissionListResponse> submissions = 
                    adminSubmissionService.getSubmissionsForProblem(problemId);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(
                            submissions,
                            "Submissions retrieved successfully"
                    ));
        } catch (Exception e) {
            log.error("Admin: Error fetching submissions: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching submissions: " + e.getMessage()));
        }
    }
    
    /**
     * Get problem statistics
     * GET /api/auth/problems/{problemId}/stats
     */
    @GetMapping("/problems/{problemId}/stats")
    @Operation(summary = "Get problem stats (Admin)", 
               description = "Get submission statistics for a problem")
    public ResponseEntity<ApiResponse<Object>> getProblemStats(
            @PathVariable Long problemId) {
        log.info("Admin: Fetching stats for problem {}", problemId);
        
        try {
            Object stats = adminSubmissionService.getProblemStats(problemId);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(
                            stats,
                            "Problem statistics retrieved successfully"
                    ));
        } catch (Exception e) {
            log.error("Admin: Error fetching stats: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching statistics: " + e.getMessage()));
        }
    }
}