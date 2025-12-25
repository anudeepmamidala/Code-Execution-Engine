package com.example.codeforge.mapper;

import com.example.codeforge.dto.behavior.BehavioralAnswerResponse;
import com.example.codeforge.dto.behavior.BehavioralQuestionResponse;
import com.example.codeforge.entity.BehavioralAnswer;
import com.example.codeforge.entity.BehavioralQuestion;

public class BehavioralMapper {

    private BehavioralMapper() {}

    public static BehavioralQuestionResponse questionToResponse(
            BehavioralQuestion question
    ) {
        return BehavioralQuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .category(question.getCategory().name())
                .build();
    }

    public static BehavioralAnswerResponse answerToResponse(
            BehavioralAnswer answer
    ) {
        return BehavioralAnswerResponse.builder()
    .answerId(answer.getId())
    .questionId(answer.getQuestion().getId())
    .questionText(answer.getQuestion().getQuestionText())
    .answerText(answer.getAnswerText())
    .wordCount(answer.getWordCount())
    .starScore(answer.getStarScore())
    .feedback(answer.getFeedback())
    .createdAt(answer.getCreatedAt())
    .build();

    }
}
