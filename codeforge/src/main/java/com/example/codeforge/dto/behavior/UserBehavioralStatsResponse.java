package com.example.codeforge.dto.behavior;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserBehavioralStatsResponse {
    private Integer totalAnswers;
    private Integer averageWordCount;
}