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
public class TestcaseResponse {
    
    private Long id;
    
    private Long problemId;
    
    private String input;
    
    private String expectedOutput;
    
    private Boolean isHidden;
    
    private LocalDateTime createdAt;
}