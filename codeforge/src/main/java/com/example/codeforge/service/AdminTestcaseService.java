package com.example.codeforge.service;

import com.example.codeforge.dto.TestcaseRequest;
import com.example.codeforge.dto.TestcaseResponse;
import com.example.codeforge.entity.Problem;
import com.example.codeforge.entity.Testcase;
import com.example.codeforge.mapper.ProblemMapper;
import com.example.codeforge.repository.ProblemRepository;
import com.example.codeforge.repository.TestcaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminTestcaseService {
    
    @Autowired
    private TestcaseRepository testcaseRepository;
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private ProblemMapper problemMapper;
    
    /**
     * Create testcase for a problem
     */
    @Transactional
    public TestcaseResponse createTestcase(TestcaseRequest request) {
        log.info("Admin: Creating testcase for problem: {}", request.getProblemId());
        
        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> {
                    log.warn("Admin: Problem not found with ID: {}", request.getProblemId());
                    return new RuntimeException("Problem not found");
                });
        
        Testcase testcase = problemMapper.testcaseToEntity(request, problem);
        Testcase savedTestcase = testcaseRepository.save(testcase);
        
        log.info("Admin: Testcase created with ID: {}", savedTestcase.getId());
        
        return problemMapper.testcaseToResponse(savedTestcase);
    }
    
    /**
     * Update testcase
     */
    @Transactional
    public TestcaseResponse updateTestcase(Long testcaseId, TestcaseRequest request) {
        log.info("Admin: Updating testcase with ID: {}", testcaseId);
        
        Testcase testcase = testcaseRepository.findById(testcaseId)
                .orElseThrow(() -> {
                    log.warn("Admin: Testcase not found with ID: {}", testcaseId);
                    return new RuntimeException("Testcase not found");
                });
        
        problemMapper.updateTestcaseEntity(request, testcase);
        Testcase updatedTestcase = testcaseRepository.save(testcase);
        
        log.info("Admin: Testcase updated with ID: {}", testcaseId);
        
        return problemMapper.testcaseToResponse(updatedTestcase);
    }
    
    /**
     * Delete testcase
     */
    @Transactional
    public void deleteTestcase(Long testcaseId) {
        log.info("Admin: Deleting testcase with ID: {}", testcaseId);
        
        Testcase testcase = testcaseRepository.findById(testcaseId)
                .orElseThrow(() -> {
                    log.warn("Admin: Testcase not found with ID: {}", testcaseId);
                    return new RuntimeException("Testcase not found");
                });
        
        testcaseRepository.delete(testcase);
        
        log.info("Admin: Testcase deleted with ID: {}", testcaseId);
    }
    
    /**
     * Get all testcases for a problem (admin view - includes hidden)
     */
    @Transactional(readOnly = true)
    public List<TestcaseResponse> getAllTestcases(Long problemId) {
        log.info("Admin: Fetching all testcases for problem: {}", problemId);
        
        List<Testcase> testcases = testcaseRepository.findByProblemId(problemId);
        
        return testcases.stream()
                .map(problemMapper::testcaseToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get hidden testcases for verification (admin view)
     */
    @Transactional(readOnly = true)
    public List<TestcaseResponse> getHiddenTestcases(Long problemId) {
        log.info("Admin: Fetching hidden testcases for problem: {}", problemId);
        
        List<Testcase> testcases = testcaseRepository.findByProblemIdAndIsHiddenTrue(problemId);
        
        return testcases.stream()
                .map(problemMapper::testcaseToResponse)
                .collect(Collectors.toList());
    }
}