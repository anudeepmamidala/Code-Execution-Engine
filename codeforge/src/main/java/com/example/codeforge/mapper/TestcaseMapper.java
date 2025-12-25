package com.example.codeforge.mapper;

import com.example.codeforge.dto.testcase.TestcaseResponse;
import com.example.codeforge.entity.Testcase;

public class TestcaseMapper {

    private TestcaseMapper() {}

    public static TestcaseResponse toResponse(Testcase testcase) {
        return TestcaseResponse.builder()
                .id(testcase.getId())
                .problemId(testcase.getProblem().getId())
                .input(testcase.getInput())
                .expectedOutput(testcase.getExpectedOutput())
                .hidden(testcase.isHidden())  // ✅ This should work
                .createdAt(testcase.getCreatedAt())
                .build();
    }
}