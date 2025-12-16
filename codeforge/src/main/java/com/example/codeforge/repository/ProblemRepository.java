package com.example.codeforge.repository;

import com.example.codeforge.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    
    List<Problem> findByDifficultyAndIsActiveTrue(String difficulty);
    
    List<Problem> findByIsActiveTrueOrderByCreatedAtDesc();
    
    @Query("SELECT p FROM Problem p WHERE p.isActive = true AND p.tags LIKE %:tag%")
    List<Problem> findByTagsAndIsActiveTrue(@Param("tag") String tag);
    
    Optional<Problem> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT p FROM Problem p WHERE p.isActive = true ORDER BY p.createdAt DESC LIMIT :limit")
    List<Problem> findRecentProblems(@Param("limit") int limit);
}