package com.example.codeforge.controller;

import com.example.codeforge.dto.SubmitCodeRequest;
import com.example.codeforge.dto.SubmissionListResponse;
import com.example.codeforge.dto.SubmissionResponse;
import com.example.codeforge.service.SubmissionService;
import com.example.codeforge.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@Tag(name = "Submissions", description = "Code submission operations")
@PreAuthorize("isAuthenticated()")
@Slf4j
public class SubmissionController {
    
    @Autowired
    private SubmissionService submissionService;
    
    /**
     * Submit code for a problem
     * POST /api/submissions
     */
    @PostMapping
    @Operation(summary = "Submit code", description = "Submit code for a problem execution")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitCode(
            Authentication authentication,
            @Valid @RequestBody SubmitCodeRequest request) {
        
        String username = authentication.getName();
        log.info("User {} submitting code for problem {}", username, request.getProblemId());
        
        try {
            SubmissionResponse submission = submissionService.submitCode(username, request);
            
            // Execute submission asynchronously
            // For MVP: execute synchronously (simpler)
            // For production: use @Async or thread pool
            new Thread(() -> {
                try {
                    submissionService.executeSubmission(submission.getId());
                } catch (Exception e) {
                    log.error("Error in async execution: {}", e.getMessage());
                }
            }).start();
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            submission,
                            "Code submitted successfully. Execution in progress..."
                    ));
        } catch (RuntimeException e) {
            log.warn("Error submitting code: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error submitting code: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error submitting code: " + e.getMessage()));
        }
    }
    
    /**
     * Get submission by ID
     * GET /api/submissions/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get submission details", description = "Retrieve submission details with test results")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionById(
            Authentication authentication,
            @PathVariable Long id) {
        
        String username = authentication.getName();
        log.info("User {} fetching submission {}", username, id);
        
        try {
            SubmissionResponse submission = submissionService.getSubmissionById(username, id);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(
                            submission,
                            "Submission retrieved successfully"
                    ));
        } catch (RuntimeException e) {
            log.warn("Submission not found: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching submission: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching submission: " + e.getMessage()));
        }
    }
    
    /**
     * Get all submissions for user
     * GET /api/submissions
     */
    @GetMapping
    @Operation(summary = "Get all my submissions", description = "Retrieve all submissions made by the current user")
    public ResponseEntity<ApiResponse<List<SubmissionListResponse>>> getUserSubmissions(
            Authentication authentication) {
        
        String username = authentication.getName();
        log.info("Fetching all submissions for user {}", username);
        
        try {
            List<SubmissionListResponse> submissions = submissionService.getUserSubmissions(username);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(
                            submissions,
                            "Submissions retrieved successfully"
                    ));
        } catch (Exception e) {
            log.error("Error fetching submissions: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching submissions: " + e.getMessage()));
        }
    }
    
    /**
     * Get submissions for a specific problem
     * GET /api/submissions/problem/{problemId}
     */
    @GetMapping("/problem/{problemId}")
    @Operation(summary = "Get my submissions for a problem", 
               description = "Retrieve all submissions for a specific problem")
    public ResponseEntity<ApiResponse<List<SubmissionListResponse>>> getUserSubmissionsForProblem(
            Authentication authentication,
            @PathVariable Long problemId) {
        
        String username = authentication.getName();
        log.info("User {} fetching submissions for problem {}", username, problemId);
        
        try {
            List<SubmissionListResponse> submissions = 
                    submissionService.getUserSubmissionsForProblem(username, problemId);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(
                            submissions,
                            "Submissions retrieved successfully"
                    ));
        } catch (RuntimeException e) {
            log.warn("Error fetching submissions: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching submissions: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching submissions: " + e.getMessage()));
        }
    }
    
    /**
     * Get latest submission for a problem
     * GET /api/submissions/problem/{problemId}/latest
     */
    @GetMapping("/problem/{problemId}/latest")
    @Operation(summary = "Get latest submission", 
               description = "Get the most recent submission for a problem")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getLatestSubmission(
            Authentication authentication,
            @PathVariable Long problemId) {
        
        String username = authentication.getName();
        log.info("User {} fetching latest submission for problem {}", username, problemId);
        
        try {
            SubmissionResponse submission = submissionService.getLatestSubmission(username, problemId);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(
                            submission,
                            "Latest submission retrieved successfully"
                    ));
        } catch (RuntimeException e) {
            log.warn("Error fetching latest submission: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching latest submission: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching submission: " + e.getMessage()));
        }
    }
}