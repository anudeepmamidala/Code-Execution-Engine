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
public class ProblemRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String examples;
    
    private String constraints;
    
    @NotBlank(message = "Difficulty is required (EASY, MEDIUM, HARD)")
    private String difficulty; // EASY, MEDIUM, HARD
    
    private String tags; // Comma-separated: array,hash-table,two-pointers
}