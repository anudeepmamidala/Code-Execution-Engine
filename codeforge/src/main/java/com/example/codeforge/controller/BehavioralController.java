package com.example.codeforge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.codeforge.dto.behavior.BehavioralAnswerRequest;
import com.example.codeforge.dto.behavior.BehavioralAnswerResponse;
import com.example.codeforge.dto.behavior.BehavioralQuestionRequest;
import com.example.codeforge.dto.behavior.BehavioralQuestionResponse;
import com.example.codeforge.dto.behavior.UserBehavioralStatsResponse;
import com.example.codeforge.service.BehavioralService;
import com.example.codeforge.utils.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/behavioral")
@RequiredArgsConstructor
@Slf4j  // ✅ ADD THIS
public class BehavioralController {

    private final BehavioralService behavioralService;

    // 🟢 USER & ADMIN - Get all behavioral questions
    @GetMapping("/questions")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<BehavioralQuestionResponse>> getQuestions() {
        log.info("Fetching all behavioral questions");
        return ApiResponse.success(
                behavioralService.getQuestions(),
                "Behavioral questions fetched"
        );
    }

    // 🟢 USER & ADMIN - Get questions by category
    @GetMapping("/questions/category/{category}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<BehavioralQuestionResponse>> getQuestionsByCategory(
            @PathVariable String category) {
        log.info("Fetching questions for category: {}", category);
        return ApiResponse.success(
                behavioralService.getQuestionsByCategory(category),
                "Questions fetched for category: " + category
        );
    }

    // 🟢 USER - Get user's answers
    @GetMapping("/my-answers")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<BehavioralAnswerResponse>> getMyAnswers(
            Authentication authentication) {
        log.info("User {} fetching their answers", authentication.getName());
        return ApiResponse.success(
                behavioralService.getUserAnswers(authentication.getName()),
                "User answers fetched"
        );
    }

    // 🟢 USER - Get user stats
    @GetMapping("/my-stats")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserBehavioralStatsResponse> getMyStats(
            Authentication authentication) {
        log.info("User {} fetching their stats", authentication.getName());
        return ApiResponse.success(
                behavioralService.getUserStats(authentication.getName()),
                "User stats fetched"
        );
    }

    // 🟢 USER - Submit answer
    @PostMapping("/answer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BehavioralAnswerResponse>> submitAnswer(
            @RequestBody BehavioralAnswerRequest request,  // ✅ FIXED import
            Authentication authentication) {
        
        log.info("User {} submitting behavioral answer", authentication.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        behavioralService.submitAnswer(
                                authentication.getName(),
                                request
                        ),
                        "Answer submitted successfully"
                ));
    }

    // 🔴 ADMIN ONLY - Create question
    @PostMapping("/questions")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BehavioralQuestionResponse>> createQuestion(
            @RequestBody BehavioralQuestionRequest request) {
        log.info("Admin creating behavioral question");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        behavioralService.createQuestion(request),
                        "Question created successfully"
                ));
    }

    // 🔴 ADMIN ONLY - Delete question
    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> deleteQuestion(@PathVariable Long questionId) {
        log.info("Admin deleting question: {}", questionId);
        behavioralService.deleteQuestion(questionId);
        return ApiResponse.success("Question deleted successfully");
    }
}