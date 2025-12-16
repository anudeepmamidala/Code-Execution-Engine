package com.example.codeforge.service;

import com.example.codeforge.client.WorkerClient;
import com.example.codeforge.dto.ExecutionJobRequest;
import com.example.codeforge.dto.ExecutionJobResponse;
import com.example.codeforge.dto.SubmitCodeRequest;
import com.example.codeforge.dto.SubmissionListResponse;
import com.example.codeforge.dto.SubmissionResponse;
import com.example.codeforge.entity.Problem;
import com.example.codeforge.entity.Submission;
import com.example.codeforge.entity.SubmissionResult;
import com.example.codeforge.entity.Testcase;
import com.example.codeforge.entity.User;
import com.example.codeforge.mapper.SubmissionMapper;
import com.example.codeforge.repository.ProblemRepository;
import com.example.codeforge.repository.SubmissionRepository;
import com.example.codeforge.repository.SubmissionResultRepository;
import com.example.codeforge.repository.TestcaseRepository;
import com.example.codeforge.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubmissionService {
    
    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private SubmissionResultRepository submissionResultRepository;
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestcaseRepository testcaseRepository;
    
    @Autowired
    private SubmissionMapper submissionMapper;
    
    @Autowired
    private WorkerClient workerClient;
    
    /**
     * Submit code for a problem
     */
    @Transactional
    public SubmissionResponse submitCode(String username, SubmitCodeRequest request) {
        log.info("User {} submitting code for problem {}", username, request.getProblemId());
        
        // Validate request
        validateSubmitCodeRequest(request);
        
        // Get user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get problem
        Problem problem = problemRepository.findByIdAndIsActiveTrue(request.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found or inactive"));
        
        // Get all testcases for this problem
        List<Testcase> testcases = testcaseRepository.findByProblemId(problem.getId());
        
        // Create submission
        Submission submission = Submission.builder()
                .user(user)
                .problem(problem)
                .code(request.getCode())
                .status("PENDING")
                .passedTestcases(0)
                .totalTestcases(testcases.size())
                .score(0)
                .build();
        
        Submission savedSubmission = submissionRepository.save(submission);
        
        log.info("Submission created with ID: {}", savedSubmission.getId());
        
        // TODO: In Phase 16, publish job to Redis queue here
        // queueService.publishExecutionJob(savedSubmission);
        
        return submissionMapper.toResponse(savedSubmission);
    }
    
    /**
     * Get submission by ID (user can only see their own)
     */
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionById(String username, Long submissionId) {
        log.info("User {} fetching submission {}", username, submissionId);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Submission submission = submissionRepository.findByIdAndUserId(submissionId, user.getId())
                .orElseThrow(() -> {
                    log.warn("Submission {} not found for user {}", submissionId, username);
                    return new RuntimeException("Submission not found");
                });
        
        List<SubmissionResult> results = submissionResultRepository.findBySubmissionId(submissionId);
        
        return submissionMapper.toResponse(submission, results);
    }
    
    /**
     * Get all submissions for a user
     */
    @Transactional(readOnly = true)
    public List<SubmissionListResponse> getUserSubmissions(String username) {
        log.info("Fetching all submissions for user {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Submission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        
        return submissions.stream()
                .map(submissionMapper::toListResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get user's submissions for a specific problem
     */
    @Transactional(readOnly = true)
    public List<SubmissionListResponse> getUserSubmissionsForProblem(String username, Long problemId) {
        log.info("Fetching submissions for user {} and problem {}", username, problemId);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Problem problem = problemRepository.findByIdAndIsActiveTrue(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        List<Submission> submissions = submissionRepository
                .findByUserIdAndProblemIdOrderByCreatedAtDesc(user.getId(), problemId);
        
        return submissions.stream()
                .map(submissionMapper::toListResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get latest submission for a problem
     */
    @Transactional(readOnly = true)
    public SubmissionResponse getLatestSubmission(String username, Long problemId) {
        log.info("Fetching latest submission for user {} and problem {}", username, problemId);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Problem problem = problemRepository.findByIdAndIsActiveTrue(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        List<Submission> submissions = submissionRepository
                .findByUserIdAndProblemIdOrderByCreatedAtDesc(user.getId(), problemId);
        
        if (submissions.isEmpty()) {
            throw new RuntimeException("No submissions found for this problem");
        }
        
        Submission submission = submissions.get(0);
        List<SubmissionResult> results = submissionResultRepository.findBySubmissionId(submission.getId());
        
        return submissionMapper.toResponse(submission, results);
    }
    
    /**
     * Update submission status and results (called by worker)
     */
    @Transactional
    public void updateSubmissionResults(Long submissionId, String status, 
                                       Integer passedTestcases, Integer totalTestcases) {
        log.info("Updating submission {} with status: {}, passed: {}/{}", 
                 submissionId, status, passedTestcases, totalTestcases);
        
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        submission.setStatus(status);
        submission.setPassedTestcases(passedTestcases);
        submission.setTotalTestcases(totalTestcases);
        
        // Calculate score (percentage)
        if (totalTestcases > 0) {
            int score = (passedTestcases * 100) / totalTestcases;
            submission.setScore(score);
        }
        
        submissionRepository.save(submission);
        
        log.info("Submission {} updated", submissionId);
    }
    
    /**
     * Save submission result (called by worker for each testcase)
     */
    @Transactional
    public void saveSubmissionResult(Long submissionId, Long testcaseId, 
                                    Boolean passed, String output, String error, 
                                    Integer executionTime) {
        log.debug("Saving result for submission {} testcase {}: passed={}", 
                  submissionId, testcaseId, passed);
        
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        Testcase testcase = testcaseRepository.findById(testcaseId)
                .orElseThrow(() -> new RuntimeException("Testcase not found"));
        
        SubmissionResult result = SubmissionResult.builder()
                .submission(submission)
                .testcase(testcase)
                .passed(passed)
                .output(output)
                .error(error)
                .executionTime(executionTime)
                .build();
        
        submissionResultRepository.save(result);
    }
    
    /**
     * Execute submission (called after creating submission)
     */
    @Transactional
    public void executeSubmission(Long submissionId) {
        log.info("Starting execution for submission: {}", submissionId);
        
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        try {
            // Check if worker is available
            if (!workerClient.isWorkerAvailable()) {
                log.warn("Worker service not available");
                submission.setStatus("FAILED");
                submission.setScore(0);
                submissionRepository.save(submission);
                return;
            }
            
            // Update status to EXECUTING
            submission.setStatus("EXECUTING");
            submissionRepository.save(submission);
            
            // Get all testcases for this problem
            List<Testcase> testcases = testcaseRepository.findByProblemId(submission.getProblem().getId());
            
            // Create execution job
            ExecutionJobRequest jobRequest = ExecutionJobRequest.builder()
                    .submissionId(submissionId)
                    .problemId(submission.getProblem().getId())
                    .code(submission.getCode())
                    .language("python")
                    .timeLimit(2000)
                    .memoryLimit(256)
                    .testcases(testcases.stream()
                            .map(tc -> ExecutionJobRequest.TestcaseData.builder()
                                    .testcaseId(tc.getId())
                                    .input(tc.getInput())
                                    .expectedOutput(tc.getExpectedOutput())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
            
            // Send to worker
            ExecutionJobResponse jobResponse = workerClient.executeCode(jobRequest);
            
            // Process results
            processExecutionResults(submission, jobResponse);
            
        } catch (Exception e) {
            log.error("Error executing submission {}: {}", submissionId, e.getMessage());
            
            submission.setStatus("FAILED");
            submission.setScore(0);
            submission.setPassedTestcases(0);
            submission.setTotalTestcases(submission.getTotalTestcases() != null ? submission.getTotalTestcases() : 0);
            submissionRepository.save(submission);
        }
    }
    
    /**
     * Process results from worker
     */
    @Transactional
    private void processExecutionResults(Submission submission, ExecutionJobResponse jobResponse) {
        log.info("Processing execution results for submission: {}", submission.getId());
        
        int passedCount = 0;
        int totalCount = jobResponse.getResults().size();
        
        // Save individual testcase results
        for (ExecutionJobResponse.TestcaseResult result : jobResponse.getResults()) {
            saveSubmissionResult(
                    submission.getId(),
                    result.getTestcaseId(),
                    result.getPassed(),
                    result.getOutput(),
                    result.getError(),
                    result.getExecutionTime()
            );
            
            if (result.getPassed()) {
                passedCount++;
            }
        }
        
        // Calculate score
        int score = (totalCount > 0) ? (passedCount * 100) / totalCount : 0;
        
        // Update submission
        submission.setStatus("COMPLETED");
        submission.setPassedTestcases(passedCount);
        submission.setTotalTestcases(totalCount);
        submission.setScore(score);
        
        submissionRepository.save(submission);
        
        log.info("Submission {} execution completed: {}/{} tests passed ({}%)", 
                submission.getId(), passedCount, totalCount, score);
    }
    
    /**
     * Validate submit code request
     */
    private void validateSubmitCodeRequest(SubmitCodeRequest request) {
        if (request.getProblemId() == null || request.getProblemId() <= 0) {
            throw new RuntimeException("Invalid problem ID");
        }
        
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new RuntimeException("Code cannot be empty");
        }
        
        if (request.getCode().length() > 1000000) { // 1MB limit
            throw new RuntimeException("Code is too large (max 1MB)");
        }
        
        // MVP: only support python
        if (!request.getLanguage().equals("python")) {
            throw new RuntimeException("Only Python is supported in MVP");
        }
    }
}