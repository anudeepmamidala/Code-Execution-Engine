package com.example.codeforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {
    
    private Long id;
    
    private Long userId;
    
    private Long problemId;
    
    private String problemTitle;
    
    private String code;
    
    private String status; // PENDING, EXECUTING, COMPLETED, FAILED
    
    private Integer passedTestcases;
    
    private Integer totalTestcases;
    
    private Integer score; // Percentage (0-100)
    
    private LocalDateTime createdAt;
    
    // Execution details
    private List<TestcaseResultResponse> results;
}