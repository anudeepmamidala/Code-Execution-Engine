package com.example.codeforge.mapper;

import com.example.codeforge.dto.SubmissionListResponse;
import com.example.codeforge.dto.SubmissionResponse;
import com.example.codeforge.dto.TestcaseResultResponse;
import com.example.codeforge.entity.Submission;
import com.example.codeforge.entity.SubmissionResult;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SubmissionMapper {
    
    // Convert Submission entity to SubmissionResponse (with results)
    public SubmissionResponse toResponse(Submission submission, List<SubmissionResult> results) {
        List<TestcaseResultResponse> resultResponses = results.stream()
                .map(this::resultToResponse)
                .collect(Collectors.toList());
        
        return SubmissionResponse.builder()
                .id(submission.getId())
                .userId(submission.getUser().getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .code(submission.getCode())
                .status(submission.getStatus())
                .passedTestcases(submission.getPassedTestcases())
                .totalTestcases(submission.getTotalTestcases())
                .score(submission.getScore())
                .createdAt(submission.getCreatedAt())
                .results(resultResponses)
                .build();
    }
    
    // Convert Submission entity to SubmissionResponse (without results)
    public SubmissionResponse toResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .userId(submission.getUser().getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .code(submission.getCode())
                .status(submission.getStatus())
                .passedTestcases(submission.getPassedTestcases())
                .totalTestcases(submission.getTotalTestcases())
                .score(submission.getScore())
                .createdAt(submission.getCreatedAt())
                .build();
    }
    
    // Convert Submission to SubmissionListResponse
    public SubmissionListResponse toListResponse(Submission submission) {
        return SubmissionListResponse.builder()
                .id(submission.getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .status(submission.getStatus())
                .passedTestcases(submission.getPassedTestcases())
                .totalTestcases(submission.getTotalTestcases())
                .score(submission.getScore())
                .createdAt(submission.getCreatedAt())
                .build();
    }
    
    // Convert SubmissionResult entity to TestcaseResultResponse
    public TestcaseResultResponse resultToResponse(SubmissionResult result) {
        return TestcaseResultResponse.builder()
                .id(result.getId())
                .submissionId(result.getSubmission().getId())
                .testcaseId(result.getTestcase().getId())
                .passed(result.getPassed())
                .output(result.getOutput())
                .error(result.getError())
                .executionTime(result.getExecutionTime())
                .createdAt(result.getCreatedAt())
                .build();
    }
}