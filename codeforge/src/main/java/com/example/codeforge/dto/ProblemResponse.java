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
public class ProblemResponse {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private String examples;
    
    private String constraints;
    
    private String difficulty;
    
    private String tags;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // For listing: include testcases count
    private Integer testcasesCount;
    
    // For detail view: include testcases (public only)
    private List<TestcaseResponse> testcases;
}