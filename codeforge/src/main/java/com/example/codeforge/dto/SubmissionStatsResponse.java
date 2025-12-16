package com.example.codeforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionStatsResponse {
    
    private Integer totalSubmissions;
    
    private Integer completedSubmissions;
    
    private Double successRate;
    
    private Double averageScore;
}