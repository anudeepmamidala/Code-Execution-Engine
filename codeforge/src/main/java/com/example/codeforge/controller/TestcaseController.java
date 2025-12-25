package com.example.codeforge.controller;

import com.example.codeforge.dto.testcase.TestcaseRequest;
import com.example.codeforge.dto.testcase.TestcaseResponse;
import com.example.codeforge.service.TestcaseService;
import com.example.codeforge.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/testcases")
@RequiredArgsConstructor
@Slf4j
public class TestcaseController {

    private final TestcaseService testcaseService;

    // 🟢 USER & ADMIN - Get public testcases for a problem
    @GetMapping("/problem/{problemId}/public")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<TestcaseResponse>> getPublicTestcases(
            @PathVariable Long problemId) {
        log.info("Fetching public testcases for problem: {}", problemId);
        return ApiResponse.success(
                testcaseService.getPublicTestcases(problemId),
                "Public testcases fetched"
        );
    }

    // 🔴 ADMIN ONLY - Get all testcases (including hidden)
    @GetMapping("/problem/{problemId}/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<List<TestcaseResponse>> getAllTestcases(
            @PathVariable Long problemId) {
        log.info("Admin fetching all testcases for problem: {}", problemId);
        return ApiResponse.success(
                testcaseService.getAllTestcases(problemId),
                "All testcases fetched"
        );
    }

    // 🔴 ADMIN ONLY - Get single testcase
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<TestcaseResponse> getTestcaseById(@PathVariable Long id) {
        log.info("Admin fetching testcase: {}", id);
        return ApiResponse.success(
                testcaseService.getTestcaseById(id),
                "Testcase fetched"
        );
    }

    // 🔴 ADMIN ONLY - Create testcase
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<TestcaseResponse>> createTestcase(
            @RequestBody TestcaseRequest request) {
        log.info("Admin creating testcase for problem: {}", request.getProblemId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        testcaseService.createTestcase(request),
                        "Testcase created successfully"
                ));
    }

    // 🔴 ADMIN ONLY - Update testcase
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<TestcaseResponse> updateTestcase(
            @PathVariable Long id,
            @RequestBody TestcaseRequest request) {
        log.info("Admin updating testcase: {}", id);
        return ApiResponse.success(
                testcaseService.updateTestcase(id, request),
                "Testcase updated successfully"
        );
    }

    // 🔴 ADMIN ONLY - Delete testcase
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> deleteTestcase(@PathVariable Long id) {
        log.info("Admin deleting testcase: {}", id);
        testcaseService.deleteTestcase(id);
        return ApiResponse.success("Testcase deleted successfully");
    }
}