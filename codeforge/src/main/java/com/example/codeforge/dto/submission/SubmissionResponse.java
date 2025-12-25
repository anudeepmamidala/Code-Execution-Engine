package com.example.codeforge.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SubmissionResponse {
    private Long submissionId;
    private Long problemId;           // ✅ ADD
    private String status;
    private String output;
    private LocalDateTime createdAt;  // ✅ ADD
}