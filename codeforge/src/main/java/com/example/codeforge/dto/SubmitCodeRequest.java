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
public class SubmitCodeRequest {
    
    @NotNull(message = "Problem ID is required")
    private Long problemId;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    private String language = "python"; // Default: python (MVP only supports python)
}