package com.example.codeforge.service;

import com.example.codeforge.dto.dashboard.DashboardSummaryResponse;
import com.example.codeforge.entity.SubmissionStatus;
import com.example.codeforge.entity.User;
import com.example.codeforge.repository.BehavioralAnswerRepository;
import com.example.codeforge.repository.SubmissionRepository;
import com.example.codeforge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final BehavioralAnswerRepository behavioralAnswerRepository;

    public DashboardSummaryResponse getSummary(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        // ---------- CODING ----------
        long totalSubmissions =
                submissionRepository.countByUserId(userId);

        long successfulSubmissions =
                submissionRepository.countByUserIdAndStatus(
                        userId, SubmissionStatus.PASSED
                );

        double passRate =
                totalSubmissions == 0
                        ? 0.0
                        : (double) successfulSubmissions / totalSubmissions * 100;

        passRate = Math.round(passRate * 100.0) / 100.0;

        // ---------- BEHAVIORAL ----------
        long behavioralAnswersCount =
                behavioralAnswerRepository.countByUserId(userId);

        Double avgWordCount =
                behavioralAnswerRepository.findAverageWordCount(userId);

        Double avgStarScore =
                behavioralAnswerRepository.findAverageStarScore(userId);

        return DashboardSummaryResponse.builder()
                .totalSubmissions((int) totalSubmissions)
                .successfulSubmissions((int) successfulSubmissions)
                .passRate(passRate)
                .behavioralAnswersCount((int) behavioralAnswersCount)
                .averageBehavioralWordCount(
                        avgWordCount == null ? 0.0 : avgWordCount
                )
                .averageStarScore(
                        avgStarScore == null ? 0.0 : avgStarScore
                )
                .build();
    }
}
