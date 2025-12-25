package com.example.codeforge.dto.behavior;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BehavioralAnswerResponse {
    private Long answerId;
    private Long questionId;
    private String questionText;
    private String answerText;
    private Integer wordCount;
    private LocalDateTime createdAt;
    private Integer starScore;
    private String feedback;

}