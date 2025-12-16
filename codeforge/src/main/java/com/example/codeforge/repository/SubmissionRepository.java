package com.example.codeforge.repository;

import com.example.codeforge.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    Optional<Submission> findByIdAndUserId(Long submissionId, Long userId);
    
    List<Submission> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Submission> findByProblemIdOrderByCreatedAtDesc(Long problemId);
    
    List<Submission> findByUserIdAndProblemIdOrderByCreatedAtDesc(Long userId, Long problemId);
    
    List<Submission> findByStatus(String status);
}