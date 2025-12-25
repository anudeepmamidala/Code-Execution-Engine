package com.example.codeforge.mapper;

import com.example.codeforge.dto.problem.ProblemResponse;
import com.example.codeforge.entity.Problem;

public class ProblemMapper {

    private ProblemMapper() {}

    public static ProblemResponse toResponse(Problem problem) {
        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .examples(problem.getExamples())           // ✅ ADD
                .constraints(problem.getConstraints())     // ✅ ADD
                .difficulty(problem.getDifficulty())
                .tags(problem.getTags())
                .build();
    }
}