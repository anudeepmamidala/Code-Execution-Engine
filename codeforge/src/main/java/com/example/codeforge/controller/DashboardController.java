package com.example.codeforge.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.codeforge.dto.dashboard.DashboardSummaryResponse;  // ✅ ADD THIS IMPORT
import com.example.codeforge.service.DashboardService;
import com.example.codeforge.utils.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j  // ✅ ADD THIS
public class DashboardController {

    private final DashboardService dashboardService;

    // 🟢 USER & ADMIN - Get dashboard summary
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")  // ✅ ADD THIS
    public ApiResponse<DashboardSummaryResponse> getSummary(
            Authentication authentication) {
        
        log.info("User {} fetching dashboard summary", authentication.getName());

        return ApiResponse.success(
                dashboardService.getSummary(authentication.getName()),
                "Dashboard summary fetched successfully"
        );
    }
}