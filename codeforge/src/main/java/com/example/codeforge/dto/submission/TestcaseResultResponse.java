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
public class TestcaseResultResponse {
    private Long testcaseId;
    private Boolean passed;
    private String output;
    private String error;
    private Integer executionTime;
    private LocalDateTime createdAt;
}