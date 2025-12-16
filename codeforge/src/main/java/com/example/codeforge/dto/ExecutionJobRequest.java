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
public class ExecutionJobRequest {
    
    private Long submissionId;
    
    private Long problemId;
    
    private String code;
    
    private List<TestcaseData> testcases;
    
    private String language = "python";
    
    private Integer timeLimit = 2000; // milliseconds
    
    private Integer memoryLimit = 256; // MB
    
    // Nested class for testcase data
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TestcaseData {
        private Long testcaseId;
        private String input;
        private String expectedOutput;
    }
}