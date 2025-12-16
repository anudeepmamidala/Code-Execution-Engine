package com.example.codeforge.service;

import com.example.codeforge.dto.ProblemListResponse;
import com.example.codeforge.dto.ProblemRequest;
import com.example.codeforge.dto.ProblemResponse;
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
public class AdminProblemService {
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private TestcaseRepository testcaseRepository;
    
    @Autowired
    private ProblemMapper problemMapper;
    
    /**
     * Get all problems (including inactive - for admin)
     */
    @Transactional(readOnly = true)
    public List<ProblemListResponse> getAllProblems() {
        log.info("Admin: Fetching all problems");
        
        List<Problem> problems = problemRepository.findAll();
        
        return problems.stream()
                .map(problem -> {
                    int testcaseCount = testcaseRepository.findByProblemId(problem.getId()).size();
                    return problemMapper.toListResponse(problem, testcaseCount);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Create new problem
     */
    @Transactional
    public ProblemResponse createProblem(ProblemRequest request) {
        log.info("Admin: Creating new problem with title: {}", request.getTitle());
        
        // Validate difficulty
        validateDifficulty(request.getDifficulty());
        
        Problem problem = problemMapper.toEntity(request);
        Problem savedProblem = problemRepository.save(problem);
        
        log.info("Admin: Problem created with ID: {}", savedProblem.getId());
        
        return problemMapper.toResponse(savedProblem, 0);
    }
    
    /**
     * Update existing problem
     */
    @Transactional
    public ProblemResponse updateProblem(Long problemId, ProblemRequest request) {
        log.info("Admin: Updating problem with ID: {}", problemId);
        
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> {
                    log.warn("Admin: Problem not found with ID: {}", problemId);
                    return new RuntimeException("Problem not found");
                });
        
        // Validate difficulty
        validateDifficulty(request.getDifficulty());
        
        problemMapper.updateEntity(request, problem);
        Problem updatedProblem = problemRepository.save(problem);
        
        log.info("Admin: Problem updated with ID: {}", problemId);
        
        List<Testcase> testcases = testcaseRepository.findByProblemId(problemId);
        return problemMapper.toResponse(updatedProblem, testcases);
    }
    
    /**
     * Delete problem (soft delete - set isActive to false)
     */
    @Transactional
    public void deleteProblem(Long problemId) {
        log.info("Admin: Soft deleting problem with ID: {}", problemId);
        
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> {
                    log.warn("Admin: Problem not found with ID: {}", problemId);
                    return new RuntimeException("Problem not found");
                });
        
        problem.setIsActive(false);
        problemRepository.save(problem);
        
        log.info("Admin: Problem soft deleted with ID: {}", problemId);
    }
    
    /**
     * Get problem details (with all testcases - public and hidden)
     */
    @Transactional(readOnly = true)
    public ProblemResponse getProblemDetails(Long problemId) {
        log.info("Admin: Fetching problem details with ID: {}", problemId);
        
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> {
                    log.warn("Admin: Problem not found with ID: {}", problemId);
                    return new RuntimeException("Problem not found");
                });
        
        List<Testcase> testcases = testcaseRepository.findByProblemId(problemId);
        
        return problemMapper.toResponse(problem, testcases);
    }
    
    /**
     * Activate problem
     */
    @Transactional
    public void activateProblem(Long problemId) {
        log.info("Admin: Activating problem with ID: {}", problemId);
        
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        problem.setIsActive(true);
        problemRepository.save(problem);
        
        log.info("Admin: Problem activated with ID: {}", problemId);
    }
    
    /**
     * Deactivate problem
     */
    @Transactional
    public void deactivateProblem(Long problemId) {
        log.info("Admin: Deactivating problem with ID: {}", problemId);
        
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        problem.setIsActive(false);
        problemRepository.save(problem);
        
        log.info("Admin: Problem deactivated with ID: {}", problemId);
    }
    
    /**
     * Validate difficulty enum
     */
    private void validateDifficulty(String difficulty) {
        if (!difficulty.matches("^(EASY|MEDIUM|HARD)$")) {
            log.warn("Invalid difficulty: {}", difficulty);
            throw new RuntimeException("Difficulty must be EASY, MEDIUM, or HARD");
        }
    }
}