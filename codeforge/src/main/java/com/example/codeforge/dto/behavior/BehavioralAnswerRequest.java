package com.example.codeforge.dto.behavior;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BehavioralAnswerRequest {
    private Long questionId;
    private String answerText;
}