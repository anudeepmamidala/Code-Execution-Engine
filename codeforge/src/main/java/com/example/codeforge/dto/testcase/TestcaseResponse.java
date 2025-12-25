package com.example.codeforge.dto.testcase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TestcaseResponse {
    private Long id;
    private Long problemId;
    private String input;
    private String expectedOutput;
    private boolean hidden;
    private LocalDateTime createdAt;
}