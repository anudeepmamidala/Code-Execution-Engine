package com.example.codeforge.mapper;

import com.example.codeforge.dto.submission.SubmissionDetailResponse;
import com.example.codeforge.dto.submission.SubmissionResponse;
import com.example.codeforge.dto.submission.TestcaseResultResponse;
import com.example.codeforge.entity.Submission;
import com.example.codeforge.entity.SubmissionResult;
import java.util.List;
import java.util.stream.Collectors;

public class SubmissionMapper {

    private SubmissionMapper() {}

    // ✅ Simple response (for list/quick view)
    public static SubmissionResponse toResponse(Submission submission) {
        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .problemId(submission.getProblem().getId())
                .status(submission.getStatus().name())
                .output(submission.getOutput())
                .createdAt(submission.getCreatedAt())
                .build();
    }

    // ✅ Detailed response with testcase results
    public static SubmissionDetailResponse toDetailResponse(
            Submission submission, 
            List<SubmissionResult> results) {
        
        int passedCount = (int) results.stream()
                .filter(SubmissionResult::getPassed)
                .count();
        
        List<TestcaseResultResponse> testcaseResults = results.stream()
                .map(result -> TestcaseResultResponse.builder()
                        .testcaseId(result.getTestcase().getId())
                        .passed(result.getPassed())
                        .output(result.getOutput())
                        .error(result.getError())
                        .executionTime(result.getExecutionTime())
                        .createdAt(result.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return SubmissionDetailResponse.builder()
        .submissionId(submission.getId())
        .problemId(submission.getProblem().getId())
        .status(submission.getStatus().name())
        .output(submission.getOutput())
        .code(submission.getCode())          // ✅ THIS WAS MISSING
        .totalTestcases(results.size())
        .passedTestcases(passedCount)
        .createdAt(submission.getCreatedAt())
        .testcaseResults(testcaseResults)
        .build();

    }
}