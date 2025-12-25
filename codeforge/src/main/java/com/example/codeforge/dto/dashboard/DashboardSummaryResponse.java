package com.example.codeforge.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {

    // Coding
    private int totalSubmissions;
    private int successfulSubmissions;
    private double passRate;

    // Behavioral
    private int behavioralAnswersCount;
    private double averageBehavioralWordCount;
    private double averageStarScore;
}
