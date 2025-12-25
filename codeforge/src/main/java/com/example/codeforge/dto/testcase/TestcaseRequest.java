package com.example.codeforge.dto.testcase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestcaseRequest {
    private Long problemId;
    private String input;
    private String expectedOutput;
    private boolean hidden;  // false = visible to users, true = hidden
}