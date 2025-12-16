package com.example.codeforge.service;

import com.example.codeforge.dto.TestcaseResponse;
import com.example.codeforge.entity.Testcase;
import com.example.codeforge.mapper.ProblemMapper;
import com.example.codeforge.repository.TestcaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TestcaseService {
    
    @Autowired
    private TestcaseRepository testcaseRepository;
    
    @Autowired
    private ProblemMapper problemMapper;
    
    /**
     * Get public testcases for a problem (for users)
     */
    @Transactional(readOnly = true)
    public List<TestcaseResponse> getPublicTestcases(Long problemId) {
        log.info("Fetching public testcases for problem: {}", problemId);
        
        List<Testcase> testcases = testcaseRepository.findByProblemIdAndIsHiddenFalse(problemId);
        
        return testcases.stream()
                .map(problemMapper::testcaseToResponse)
                .collect(Collectors.toList());
    }
}