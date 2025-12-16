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
public class SubmissionListResponse {
    
    private Long id;
    
    private Long problemId;
    
    private String problemTitle;
    
    private String status;
    
    private Integer passedTestcases;
    
    private Integer totalTestcases;
    
    private Integer score;
    
    private LocalDateTime createdAt;
}