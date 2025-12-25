package com.example.codeforge.service;

import com.example.codeforge.dto.testcase.TestcaseRequest;
import com.example.codeforge.dto.testcase.TestcaseResponse;
import com.example.codeforge.entity.Problem;
import com.example.codeforge.entity.Testcase;
import com.example.codeforge.mapper.TestcaseMapper;
import com.example.codeforge.repository.ProblemRepository;
import com.example.codeforge.repository.TestcaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TestcaseService {
    
    private final TestcaseRepository testcaseRepository;
    private final ProblemRepository problemRepository;
    
    // ✅ GET PUBLIC TESTCASES (Users can see)
    public List<TestcaseResponse> getPublicTestcases(Long problemId) {
        log.info("Fetching public testcases for problem: {}", problemId);
        
        if (problemId == null) {
            throw new IllegalArgumentException("Problem ID cannot be null");
        }
        
        List<Testcase> testcases = testcaseRepository.findByProblemIdAndHiddenFalse(problemId);
        log.debug("Found {} public testcases for problem {}", testcases.size(), problemId);
        
        return testcases.stream()
                .map(TestcaseMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    // ✅ GET ALL TESTCASES (Admin can see all including hidden)
    public List<TestcaseResponse> getAllTestcases(Long problemId) {
        log.info("Fetching all testcases (including hidden) for problem: {}", problemId);
        
        if (problemId == null) {
            throw new IllegalArgumentException("Problem ID cannot be null");
        }
        
        List<Testcase> testcases = testcaseRepository.findByProblemId(problemId);
        log.debug("Found {} total testcases for problem {}", testcases.size(), problemId);
        
        return testcases.stream()
                .map(TestcaseMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    // ✅ GET SINGLE TESTCASE BY ID
    public TestcaseResponse getTestcaseById(Long id) {
        log.info("Fetching testcase: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Testcase ID cannot be null");
        }
        
        Testcase testcase = testcaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testcase not found"));
        return TestcaseMapper.toResponse(testcase);
    }
    
    // ✅ CREATE TESTCASE (ADMIN ONLY)
    public TestcaseResponse createTestcase(TestcaseRequest request) {
        log.info("Creating testcase for problem: {}", request.getProblemId());
        
        // ✅ Validate inputs
        if (request.getProblemId() == null) {
            throw new IllegalArgumentException("Problem ID cannot be null");
        }
        
        if (request.getInput() == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (request.getExpectedOutput() == null) {
            throw new IllegalArgumentException("Expected output cannot be null");
        }
        
        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        Testcase testcase = Testcase.builder()
                .problem(problem)
                .input(request.getInput())
                .expectedOutput(request.getExpectedOutput())
                .hidden(request.isHidden())
                .createdAt(LocalDateTime.now())
                .build();
        
        Testcase saved = testcaseRepository.save(testcase);
        log.info("Testcase created with ID: {} for problem {}", saved.getId(), problem.getId());
        return TestcaseMapper.toResponse(saved);
    }
    
    // ✅ UPDATE TESTCASE (ADMIN ONLY)
    public TestcaseResponse updateTestcase(Long id, TestcaseRequest request) {
        log.info("Updating testcase: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Testcase ID cannot be null");
        }
        
        // ✅ Validate inputs
        if (request.getInput() == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (request.getExpectedOutput() == null) {
            throw new IllegalArgumentException("Expected output cannot be null");
        }
        
        Testcase testcase = testcaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testcase not found"));
        
        testcase.setInput(request.getInput());
        testcase.setExpectedOutput(request.getExpectedOutput());
        testcase.setHidden(request.isHidden());
        
        Testcase updated = testcaseRepository.save(testcase);
        log.info("Testcase {} updated successfully", id);
        return TestcaseMapper.toResponse(updated);
    }
    
    // ✅ DELETE TESTCASE (ADMIN ONLY)
    public void deleteTestcase(Long id) {
        log.info("Deleting testcase: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Testcase ID cannot be null");
        }
        
        Testcase testcase = testcaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testcase not found"));
        
        testcaseRepository.delete(testcase);
        log.info("Testcase {} deleted successfully", id);
    }
}