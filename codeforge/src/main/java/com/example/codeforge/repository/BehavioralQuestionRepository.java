package com.example.codeforge.repository;

import com.example.codeforge.entity.BehavioralQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository  // ✅ ADD THIS
public interface BehavioralQuestionRepository
        extends JpaRepository<BehavioralQuestion, Long> {

    List<BehavioralQuestion> findByIsActiveTrue();
}