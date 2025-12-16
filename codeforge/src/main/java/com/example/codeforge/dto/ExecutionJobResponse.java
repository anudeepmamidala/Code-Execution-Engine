package com.example.codeforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionJobResponse {
    
    private Long submissionId;
    
    private String status; // COMPLETED, FAILED
    
    private List<TestcaseResult> results;
    
    private String error;
    
    // Nested class for testcase result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TestcaseResult {
        private Long testcaseId;
        private Boolean passed;
        private String output;
        private String error;
        private Integer executionTime;
    }
}