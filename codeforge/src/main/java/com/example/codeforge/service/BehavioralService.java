package com.example.codeforge.service;

import com.example.codeforge.dto.behavior.*;
import com.example.codeforge.entity.*;
import com.example.codeforge.mapper.BehavioralMapper;
import com.example.codeforge.repository.BehavioralAnswerRepository;
import com.example.codeforge.repository.BehavioralQuestionRepository;
import com.example.codeforge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BehavioralService {

    private final BehavioralQuestionRepository questionRepository;
    private final BehavioralAnswerRepository answerRepository;
    private final UserRepository userRepository;

    // ================= QUESTIONS =================

    public List<BehavioralQuestionResponse> getQuestions() {
        return questionRepository.findByIsActiveTrue()
                .stream()
                .map(BehavioralMapper::questionToResponse)
                .collect(Collectors.toList());
    }

    public List<BehavioralQuestionResponse> getQuestionsByCategory(String category) {
        BehavioralCategory cat =
                BehavioralCategory.valueOf(category.toUpperCase());

        return questionRepository.findByIsActiveTrue()
                .stream()
                .filter(q -> q.getCategory() == cat)
                .map(BehavioralMapper::questionToResponse)
                .collect(Collectors.toList());
    }

    // ================= ANSWERS =================

    public BehavioralAnswerResponse submitAnswer(
            String username,
            BehavioralAnswerRequest request
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BehavioralQuestion question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        int starScore = calculateStarScore(request.getAnswerText());
        String feedback = buildFeedback(starScore);

        BehavioralAnswer answer = BehavioralAnswer.builder()
                .user(user)
                .question(question)
                .answerText(request.getAnswerText())
                .starScore(starScore)
                .feedback(feedback)
                .build();

        answer = answerRepository.save(answer);

        return BehavioralMapper.answerToResponse(answer);
    }

    public List<BehavioralAnswerResponse> getUserAnswers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return answerRepository.findByUserId(user.getId())
                .stream()
                .map(BehavioralMapper::answerToResponse)
                .collect(Collectors.toList());
    }

    // ================= ADMIN =================

    public BehavioralQuestionResponse createQuestion(BehavioralQuestionRequest request) {
        BehavioralCategory category =
                BehavioralCategory.valueOf(request.getCategory().toUpperCase());

        BehavioralQuestion question = BehavioralQuestion.builder()
                .questionText(request.getQuestionText())
                .category(category)
                .isActive(true)
                .build();

        return BehavioralMapper.questionToResponse(
                questionRepository.save(question)
        );
    }

    public void deleteQuestion(Long questionId) {
        BehavioralQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setIsActive(false);
        questionRepository.save(question);
    }

    // ================= STATS =================

    public UserBehavioralStatsResponse getUserStats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalAnswers = answerRepository.countByUserId(user.getId());
        Double avgWordCount = answerRepository.findAverageWordCount(user.getId());

        return UserBehavioralStatsResponse.builder()
                .totalAnswers((int) totalAnswers)
                .averageWordCount(avgWordCount == null ? 0 : avgWordCount.intValue())
                .build();
    }

    // ================= EVALUATION =================

    private int calculateStarScore(String text) {
        if (text == null) return 0;

        int score = 0;
        String lower = text.toLowerCase();

        if (lower.contains("situation")) score++;
        if (lower.contains("task")) score++;
        if (lower.contains("action")) score++;
        if (lower.contains("result") || lower.contains("outcome")) score++;

        return score;
    }

    private String buildFeedback(int starScore) {
        return switch (starScore) {
            case 0 -> "Answer lacks structure. Use the STAR method.";
            case 1, 2 -> "Decent start. Add clearer situation and results.";
            case 3 -> "Good answer. Minor improvements possible.";
            case 4 -> "Excellent STAR-structured answer.";
            default -> "Not evaluated.";
        };
    }
}
