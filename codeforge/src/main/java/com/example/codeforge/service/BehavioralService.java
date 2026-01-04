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
        log.info("Fetching all active behavioral questions");
        List<BehavioralQuestionResponse> questions = questionRepository.findByIsActiveTrue()
                .stream()
                .map(BehavioralMapper::questionToResponse)
                .collect(Collectors.toList());
        log.info("Found {} active questions", questions.size());
        return questions;
    }

    public List<BehavioralQuestionResponse> getQuestionsByCategory(String category) {
        log.info("Fetching questions by category: {}", category);
        try {
            BehavioralCategory cat =
                    BehavioralCategory.valueOf(category.toUpperCase());

            List<BehavioralQuestionResponse> questions = questionRepository.findByIsActiveTrue()
                    .stream()
                    .filter(q -> q.getCategory() == cat)
                    .map(BehavioralMapper::questionToResponse)
                    .collect(Collectors.toList());
            
            log.info("Found {} questions for category {}", questions.size(), category);
            return questions;
        } catch (IllegalArgumentException e) {
            log.error("Invalid category: {}", category);
            throw new RuntimeException("Invalid category: " + category);
        }
    }

    // ================= ANSWERS =================

    public BehavioralAnswerResponse submitAnswer(
            String username,
            BehavioralAnswerRequest request
    ) {
        log.info("User {} submitting behavioral answer for question {}", username, request.getQuestionId());
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        BehavioralQuestion question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> {
                    log.error("Question not found: {}", request.getQuestionId());
                    return new RuntimeException("Question not found");
                });

        int starScore = calculateStarScore(request.getAnswerText());
        String feedback = buildFeedback(starScore);

        log.debug("Answer evaluated - Star Score: {}, Feedback: {}", starScore, feedback);

        BehavioralAnswer answer = BehavioralAnswer.builder()
                .user(user)
                .question(question)
                .answerText(request.getAnswerText())
                .starScore(starScore)
                .feedback(feedback)
                .build();

        answer = answerRepository.save(answer);
        
        log.info("Answer saved with ID: {} - Score: {}", answer.getId(), starScore);

        return BehavioralMapper.answerToResponse(answer);
    }

    public List<BehavioralAnswerResponse> getUserAnswers(String username) {
        log.info("Fetching answers for user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        List<BehavioralAnswerResponse> answers = answerRepository.findByUserId(user.getId())
                .stream()
                .map(BehavioralMapper::answerToResponse)
                .collect(Collectors.toList());
        
        log.info("Found {} answers for user {}", answers.size(), username);
        return answers;
    }

    // ================= ADMIN =================

    public BehavioralQuestionResponse createQuestion(BehavioralQuestionRequest request) {
        log.info("Admin creating behavioral question: {}", request.getQuestionText());
        
        try {
            BehavioralCategory category =
                    BehavioralCategory.valueOf(request.getCategory().toUpperCase());

            BehavioralQuestion question = BehavioralQuestion.builder()
                    .questionText(request.getQuestionText())
                    .category(category)
                    .isActive(true)
                    .build();

            BehavioralQuestion saved = questionRepository.save(question);
            log.info("Question created with ID: {}", saved.getId());
            
            return BehavioralMapper.questionToResponse(saved);
        } catch (IllegalArgumentException e) {
            log.error("Invalid category: {}", request.getCategory());
            throw new RuntimeException("Invalid category: " + request.getCategory());
        }
    }

    public void deleteQuestion(Long questionId) {
        log.info("Admin deleting question: {}", questionId);
        
        BehavioralQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> {
                    log.error("Question not found: {}", questionId);
                    return new RuntimeException("Question not found");
                });

        question.setIsActive(false);
        questionRepository.save(question);
        
        log.info("Question {} soft-deleted", questionId);
    }

    // ================= STATS =================

    public UserBehavioralStatsResponse getUserStats(String username) {
        log.info("Fetching behavioral stats for user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        long totalAnswers = answerRepository.countByUserId(user.getId());
        Double avgWordCount = answerRepository.findAverageWordCount(user.getId());
        
        int avgWordCountInt = avgWordCount == null ? 0 : avgWordCount.intValue();
        
        log.debug("User {} stats - Total Answers: {}, Avg Word Count: {}", 
                username, totalAnswers, avgWordCountInt);

        return UserBehavioralStatsResponse.builder()
                .totalAnswers((int) totalAnswers)
                .averageWordCount(avgWordCountInt)
                .build();
    }

    // ================= EVALUATION =================

    private int calculateStarScore(String text) {
        if (text == null || text.isBlank()) return 0;

        int score = 0;
        String lower = text.toLowerCase();

        // ✅ Check for STAR method components
        if (hasWord(lower, "situation")) score++;
        if (hasWord(lower, "task")) score++;
        if (hasWord(lower, "action")) score++;
        if (hasWord(lower, "result") || hasWord(lower, "outcome")) score++;

        log.debug("STAR score calculated: {} from text length: {}", score, text.length());
        return score;
    }

    // ✅ Helper method for word boundary checking
    private boolean hasWord(String text, String word) {
        return text.matches("(?i).*\\b" + word + "\\b.*");
    }

    private String buildFeedback(int starScore) {
        return switch (starScore) {
            case 0 -> "Answer lacks structure. Consider using the STAR method (Situation, Task, Action, Result).";
            case 1 -> "Good start! Add more details about the situation or task.";
            case 2 -> "Decent answer. Include clearer actions and results.";
            case 3 -> "Good answer using STAR method. Minor improvements possible.";
            case 4 -> "Excellent STAR-structured answer with clear details!";
            default -> "Not evaluated.";
        };
    }
}