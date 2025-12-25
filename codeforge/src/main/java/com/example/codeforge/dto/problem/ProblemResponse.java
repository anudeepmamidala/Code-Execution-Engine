package com.example.codeforge.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProblemResponse {
    private Long id;
    private String title;
    private String description;
    private String examples;
    private String constraints;
    private String difficulty;
    private String tags;
}