package com.example.codeforge.dto.problem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProblemRequest {
    private String title;
    private String description;
    private String examples;
    private String constraints;
    private String difficulty;
    private String tags;
}
