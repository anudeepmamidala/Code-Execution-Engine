package com.example.codeforge.service;

import com.example.codeforge.dto.submission.*;
import com.example.codeforge.entity.*;
import com.example.codeforge.mapper.SubmissionMapper;
import com.example.codeforge.repository.*;
import com.example.codeforge.utils.CodeExecutionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubmissionService {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final TestcaseRepository testcaseRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionResultRepository submissionResultRepository;

    @Value("${execution.enabled:true}")
    private boolean executionEnabled;

    public SubmissionResponse submit(String username, SubmitCodeRequest request) {

        if (request.getProblemId() == null)
            throw new IllegalArgumentException("Problem ID must not be null");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        if (request.getCode() == null || request.getCode().isBlank())
            throw new IllegalArgumentException("Code cannot be empty");

        Submission submission = Submission.builder()
                .user(user)
                .problem(problem)
                .code(request.getCode())
                .status(SubmissionStatus.PENDING)
                .build();

        submission = submissionRepository.save(submission);

        List<Testcase> testcases =
                testcaseRepository.findByProblemIdAndHiddenFalse(problem.getId());

        if (testcases.isEmpty()) {
            submission.setStatus(SubmissionStatus.ERROR);
            submission.setOutput("No testcases configured");
            return SubmissionMapper.toResponse(submission);
        }

        if (!executionEnabled) {
            submission.setStatus(SubmissionStatus.ERROR);
            submission.setOutput("Code execution disabled");
            return SubmissionMapper.toResponse(submission);
        }

        executeAndSaveResults(submission, testcases);
        return SubmissionMapper.toResponse(submission);
    }

    public SubmissionDetailResponse getSubmissionDetails(
            String username, Long submissionId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Submission submission =
                submissionRepository.findByIdAndUserId(submissionId, user.getId())
                        .orElseThrow(() -> new RuntimeException("Submission not found"));

        List<SubmissionResult> results =
                submissionResultRepository.findBySubmissionId(submissionId);

        return SubmissionMapper.toDetailResponse(submission, results);
    }

    private void executeAndSaveResults(
            Submission submission, List<Testcase> testcases) {

        boolean hasError = false;
        boolean hasTimeout = false;
        boolean allPassed = true;
        int passedCount = 0;

        for (Testcase tc : testcases) {

            long start = System.currentTimeMillis();

            CodeExecutionUtil.ExecutionResult result =
                    CodeExecutionUtil.executeAndJudgePython(
                            submission.getCode(),
                            tc.getInput(),
                            tc.getExpectedOutput()
                    );

            long execTime = System.currentTimeMillis() - start;

            if ("ERROR".equals(result.verdict())) {
                hasError = true;
                allPassed = false;
            } else if ("TIMEOUT".equals(result.verdict())) {
                hasTimeout = true;
                allPassed = false;
            } else if (!result.passed()) {
                allPassed = false;
            } else {
                passedCount++;
            }

            SubmissionResult submissionResult = SubmissionResult.builder()
                    .submission(submission)
                    .testcase(tc)
                    .passed(result.passed())
                    .output(result.stdout())
                    .error(result.stderr())
                    .executionTime((int) execTime)
                    .build();

            submissionResultRepository.save(submissionResult);
        }

        if (hasError) {
            submission.setStatus(SubmissionStatus.ERROR);
            submission.setOutput("Runtime Error");
        } else if (hasTimeout) {
            submission.setStatus(SubmissionStatus.FAILED);
            submission.setOutput("Time Limit Exceeded");
        } else if (allPassed) {
            submission.setStatus(SubmissionStatus.PASSED);
            submission.setOutput("Accepted");
        } else {
            submission.setStatus(SubmissionStatus.FAILED);
            submission.setOutput(passedCount + "/" + testcases.size() + " testcases passed");
        }

        submissionRepository.save(submission);
    }

    public List<SubmissionResponse> getUserSubmissions(String username) {
        return submissionRepository.findByUserUsername(username)
                .stream()
                .map(SubmissionMapper::toResponse)
                .toList();
    }
}
