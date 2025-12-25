package com.example.codeforge.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SubmissionDetailResponse {
    private Long submissionId;
    private Long problemId;
    private String status;
    private String output;
    private String code;

    private Integer totalTestcases;
    private Integer passedTestcases;
    private LocalDateTime createdAt;
    private List<TestcaseResultResponse> testcaseResults;  // ✅ Detailed results
}