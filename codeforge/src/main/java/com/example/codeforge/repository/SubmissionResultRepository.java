package com.example.codeforge.repository;

import com.example.codeforge.entity.SubmissionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubmissionResultRepository extends JpaRepository<SubmissionResult, Long> {
    
    List<SubmissionResult> findBySubmissionId(Long submissionId);
    
    long countBySubmissionIdAndPassed(Long submissionId, Boolean passed);
}