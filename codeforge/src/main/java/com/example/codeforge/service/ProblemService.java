package com.example.codeforge.service;

import com.example.codeforge.dto.ProblemListResponse;
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
public class ProblemService {
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private TestcaseRepository testcaseRepository;
    
    @Autowired
    private ProblemMapper problemMapper;
    
    /**
     * Get all active problems (for users)
     */
    @Transactional(readOnly = true)
    public List<ProblemListResponse> getAllProblems() {
        log.info("Fetching all active problems");
        
        List<Problem> problems = problemRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        
        return problems.stream()
                .map(problem -> {
                    int testcaseCount = testcaseRepository.findByProblemIdAndIsHiddenFalse(problem.getId()).size();
                    return problemMapper.toListResponse(problem, testcaseCount);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get problems by difficulty
     */
    @Transactional(readOnly = true)
    public List<ProblemListResponse> getProblemsByDifficulty(String difficulty) {
        log.info("Fetching problems by difficulty: {}", difficulty);
        
        List<Problem> problems = problemRepository.findByDifficultyAndIsActiveTrue(difficulty);
        
        return problems.stream()
                .map(problem -> {
                    int testcaseCount = testcaseRepository.findByProblemIdAndIsHiddenFalse(problem.getId()).size();
                    return problemMapper.toListResponse(problem, testcaseCount);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get problems by tag
     */
    @Transactional(readOnly = true)
    public List<ProblemListResponse> getProblemsByTag(String tag) {
        log.info("Fetching problems by tag: {}", tag);
        
        List<Problem> problems = problemRepository.findByTagsAndIsActiveTrue(tag);
        
        return problems.stream()
                .map(problem -> {
                    int testcaseCount = testcaseRepository.findByProblemIdAndIsHiddenFalse(problem.getId()).size();
                    return problemMapper.toListResponse(problem, testcaseCount);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get problem by ID with public testcases
     */
    @Transactional(readOnly = true)
    public ProblemResponse getProblemById(Long problemId) {
        log.info("Fetching problem with ID: {}", problemId);
        
        Problem problem = problemRepository.findByIdAndIsActiveTrue(problemId)
                .orElseThrow(() -> {
                    log.warn("Problem not found with ID: {}", problemId);
                    return new RuntimeException("Problem not found");
                });
        
        // Get only public testcases
        List<Testcase> publicTestcases = testcaseRepository.findByProblemIdAndIsHiddenFalse(problemId);
        
        return problemMapper.toResponse(problem, publicTestcases);
    }
    
    /**
     * Get recent problems
     */
    @Transactional(readOnly = true)
    public List<ProblemListResponse> getRecentProblems(int limit) {
        log.info("Fetching {} recent problems", limit);
        
        List<Problem> problems = problemRepository.findRecentProblems(limit);
        
        return problems.stream()
                .map(problem -> {
                    int testcaseCount = testcaseRepository.findByProblemIdAndIsHiddenFalse(problem.getId()).size();
                    return problemMapper.toListResponse(problem, testcaseCount);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get all testcases for a problem (both public and hidden - for worker)
     */
    @Transactional(readOnly = true)
    public List<Testcase> getAllTestcasesForProblem(Long problemId) {
        log.debug("Fetching all testcases for problem: {}", problemId);
        
        Problem problem = problemRepository.findByIdAndIsActiveTrue(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        return testcaseRepository.findByProblemId(problemId);
    }
}