package com.example.codeforge.mapper;

import com.example.codeforge.dto.ProblemListResponse;
import com.example.codeforge.dto.ProblemRequest;
import com.example.codeforge.dto.ProblemResponse;
import com.example.codeforge.dto.TestcaseRequest;
import com.example.codeforge.dto.TestcaseResponse;
import com.example.codeforge.entity.Problem;
import com.example.codeforge.entity.Testcase;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProblemMapper {
    
    // Convert ProblemRequest to Problem entity
    public Problem toEntity(ProblemRequest request) {
        return Problem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .examples(request.getExamples())
                .constraints(request.getConstraints())
                .difficulty(request.getDifficulty())
                .tags(request.getTags())
                .isActive(true)
                .build();
    }
    
    // Update existing Problem entity from ProblemRequest
    public void updateEntity(ProblemRequest request, Problem problem) {
        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setExamples(request.getExamples());
        problem.setConstraints(request.getConstraints());
        problem.setDifficulty(request.getDifficulty());
        problem.setTags(request.getTags());
    }
    
    // Convert Problem entity to ProblemResponse (with testcases)
    public ProblemResponse toResponse(Problem problem, List<Testcase> testcases) {
        List<TestcaseResponse> testcaseResponses = testcases.stream()
                .filter(tc -> !tc.getIsHidden()) // Only public testcases
                .map(this::testcaseToResponse)
                .collect(Collectors.toList());
        
        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .examples(problem.getExamples())
                .constraints(problem.getConstraints())
                .difficulty(problem.getDifficulty())
                .tags(problem.getTags())
                .isActive(problem.getIsActive())
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .testcasesCount(testcases.size())
                .testcases(testcaseResponses)
                .build();
    }
    
    // Convert Problem entity to ProblemResponse (without testcases - for listing)
    public ProblemResponse toResponse(Problem problem, Integer testcasesCount) {
        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .examples(problem.getExamples())
                .constraints(problem.getConstraints())
                .difficulty(problem.getDifficulty())
                .tags(problem.getTags())
                .isActive(problem.getIsActive())
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .testcasesCount(testcasesCount)
                .build();
    }
    
    // Convert Problem to ProblemListResponse
    public ProblemListResponse toListResponse(Problem problem, Integer testcasesCount) {
        return ProblemListResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .tags(problem.getTags())
                .testcasesCount(testcasesCount)
                .createdAt(problem.getCreatedAt())
                .isActive(problem.getIsActive())
                .build();
    }
    
    // Convert Testcase entity to TestcaseResponse
    public TestcaseResponse testcaseToResponse(Testcase testcase) {
        return TestcaseResponse.builder()
                .id(testcase.getId())
                .problemId(testcase.getProblem().getId())
                .input(testcase.getInput())
                .expectedOutput(testcase.getExpectedOutput())
                .isHidden(testcase.getIsHidden())
                .createdAt(testcase.getCreatedAt())
                .build();
    }
    
    // Convert TestcaseRequest to Testcase entity
    public Testcase testcaseToEntity(TestcaseRequest request, Problem problem) {
        return Testcase.builder()
                .problem(problem)
                .input(request.getInput())
                .expectedOutput(request.getExpectedOutput())
                .isHidden(request.getIsHidden())
                .build();
    }
    
    // Update existing Testcase from TestcaseRequest
    public void updateTestcaseEntity(TestcaseRequest request, Testcase testcase) {
        testcase.setInput(request.getInput());
        testcase.setExpectedOutput(request.getExpectedOutput());
        testcase.setIsHidden(request.getIsHidden());
    }
}