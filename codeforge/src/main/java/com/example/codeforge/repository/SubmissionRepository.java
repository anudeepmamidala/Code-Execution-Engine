package com.example.codeforge.repository;

import com.example.codeforge.entity.Submission;
import com.example.codeforge.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // Lists
    List<Submission> findByUserId(Long userId);
    List<Submission> findByUserUsername(String username);

    Optional<Submission> findByIdAndUserId(Long submissionId, Long userId);

    // Counts (USE LONG ONLY)
    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, SubmissionStatus status);
}
