package com.example.codeforge.service;

import com.example.codeforge.dto.SubmissionListResponse;
import com.example.codeforge.dto.SubmissionResponse;
import com.example.codeforge.dto.SubmissionStatsResponse;
import com.example.codeforge.entity.Submission;
import com.example.codeforge.entity.SubmissionResult;
import com.example.codeforge.mapper.SubmissionMapper;
import com.example.codeforge.repository.SubmissionRepository;
import com.example.codeforge.repository.SubmissionResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminSubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private SubmissionResultRepository submissionResultRepository;

    @Autowired
    private SubmissionMapper submissionMapper;

    /**
     * Get all submissions (admin view)
     */
    @Transactional(readOnly = true)
    public List<SubmissionListResponse> getAllSubmissions() {
        log.info("Admin: Fetching all submissions");

        return submissionRepository.findAll()
                .stream()
                .map(submissionMapper::toListResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get submission details (admin can see any submission)
     */
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionDetails(Long submissionId) {
        log.info("Admin: Fetching submission {}", submissionId);

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> {
                    log.warn("Admin: Submission {} not found", submissionId);
                    return new RuntimeException("Submission not found");
                });

        List<SubmissionResult> results =
                submissionResultRepository.findBySubmissionId(submissionId);

        return submissionMapper.toResponse(submission, results);
    }

    /**
     * Get submissions by status
     */
    @Transactional(readOnly = true)
    public List<SubmissionListResponse> getSubmissionsByStatus(String status) {
        log.info("Admin: Fetching submissions by status {}", status);

        if (!status.matches("^(PENDING|EXECUTING|COMPLETED|FAILED)$")) {
            throw new RuntimeException("Invalid status");
        }

        return submissionRepository.findByStatus(status)
                .stream()
                .map(submissionMapper::toListResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get submissions for a problem
     */
    @Transactional(readOnly = true)
    public List<SubmissionListResponse> getSubmissionsForProblem(Long problemId) {
        log.info("Admin: Fetching submissions for problem {}", problemId);

        return submissionRepository
                .findByProblemIdOrderByCreatedAtDesc(problemId)
                .stream()
                .map(submissionMapper::toListResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get statistics for a problem
     */
    @Transactional(readOnly = true)
    public SubmissionStatsResponse getProblemStats(Long problemId) {
        log.info("Admin: Fetching stats for problem {}", problemId);

        List<Submission> submissions =
                submissionRepository.findByProblemIdOrderByCreatedAtDesc(problemId);

        if (submissions.isEmpty()) {
            return SubmissionStatsResponse.builder()
                    .totalSubmissions(0)
                    .completedSubmissions(0)
                    .successRate(0.0)
                    .averageScore(0.0)
                    .build();
        }

        long completedCount = submissions.stream()
                .filter(s -> "COMPLETED".equals(s.getStatus()))
                .count();

        double successRate =
                (completedCount * 100.0) / submissions.size();

        double averageScore = submissions.stream()
                .filter(s -> "COMPLETED".equals(s.getStatus()))
                .filter(s -> s.getScore() != null)
                .mapToInt(Submission::getScore)
                .average()
                .orElse(0.0);

        return SubmissionStatsResponse.builder()
                .totalSubmissions(submissions.size())
                .completedSubmissions((int) completedCount)
                .successRate(successRate)
                .averageScore(averageScore)
                .build();
    }
}
