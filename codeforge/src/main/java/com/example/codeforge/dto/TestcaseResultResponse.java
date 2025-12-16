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
public class TestcaseResultResponse {
    
    private Long id;
    
    private Long submissionId;
    
    private Long testcaseId;
    
    private Boolean passed;
    
    private String output;
    
    private String error;
    
    private Integer executionTime; // milliseconds
    
    private LocalDateTime createdAt;
}