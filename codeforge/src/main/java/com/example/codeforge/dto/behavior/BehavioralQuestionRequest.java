package com.example.codeforge.dto.behavior;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BehavioralQuestionRequest {
    private String questionText;
    private String category;  // HR, Leadership, Conflict, Failure
}