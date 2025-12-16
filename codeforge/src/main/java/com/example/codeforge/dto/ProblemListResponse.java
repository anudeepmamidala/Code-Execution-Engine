package com.example.codeforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemListResponse {
    
    private Long id;
    
    private String title;
    
    private String difficulty;
    
    private String tags;
    
    private Integer testcasesCount;
    
    private LocalDateTime createdAt;
    
    // For admin list view
    private Boolean isActive;
}