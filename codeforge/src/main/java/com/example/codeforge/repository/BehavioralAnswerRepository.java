package com.example.codeforge.repository;

import com.example.codeforge.entity.BehavioralAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository  // ✅ ADD THIS
public interface BehavioralAnswerRepository
        extends JpaRepository<BehavioralAnswer, Long> {

    List<BehavioralAnswer> findByUserId(Long userId);  // ✅ ADD THIS

    long countByUserId(Long userId);

    @Query("SELECT AVG(b.wordCount) FROM BehavioralAnswer b WHERE b.user.id = :userId")
    Double findAverageWordCount(Long userId);

    @Query("SELECT AVG(b.starScore) FROM BehavioralAnswer b WHERE b.user.id = :userId")
    Double findAverageStarScore(Long userId);

}