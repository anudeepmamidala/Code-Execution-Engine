package com.example.codeforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestcaseRequest {
    
    @NotNull(message = "Problem ID is required")
    private Long problemId;
    
    @NotBlank(message = "Input is required")
    private String input;
    
    @NotBlank(message = "Expected output is required")
    private String expectedOutput;
    
    private Boolean isHidden = false; // Default: public testcase
}