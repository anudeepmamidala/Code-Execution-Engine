package com.example.codeforge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.codeforge.dto.problem.ProblemRequest;
import com.example.codeforge.dto.problem.ProblemResponse;
import com.example.codeforge.service.ProblemService;
import com.example.codeforge.utils.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@Slf4j
public class ProblemController {

    private final ProblemService problemService;

    // 🟢 USER & ADMIN - View all problems
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<ProblemResponse>> getAllProblems() {
        log.info("Fetching all problems");
        return ApiResponse.success(
                problemService.getAllProblems(),
                "Problems fetched"
        );
    }

    // 🟢 USER & ADMIN - View single problem
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ProblemResponse> getProblem(@PathVariable Long id) {
        log.info("Fetching problem {}", id);
        return ApiResponse.success(
                problemService.getProblemById(id),
                "Problem fetched"
        );
    }

    // 🔴 ADMIN ONLY - Create problem
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProblemResponse>> createProblem(
            @RequestBody ProblemRequest request) {
        log.info("Admin creating problem: {}", request.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        problemService.createProblem(request),
                        "Problem created successfully"
                ));
    }

    // 🔴 ADMIN ONLY - Update problem
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<ProblemResponse> updateProblem(
            @PathVariable Long id,
            @RequestBody ProblemRequest request) {
        log.info("Admin updating problem {}", id);
        return ApiResponse.success(
                problemService.updateProblem(id, request),
                "Problem updated successfully"
        );
    }

    // 🔴 ADMIN ONLY - Delete problem
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> deleteProblem(@PathVariable Long id) {
        log.info("Admin deleting problem {}", id);
        problemService.deleteProblem(id);
        return ApiResponse.success("Problem deleted successfully");
    }
}