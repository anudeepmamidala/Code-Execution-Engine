package com.example.codeforge.repository;

import com.example.codeforge.entity.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestcaseRepository extends JpaRepository<Testcase, Long> {

    List<Testcase> findByProblemId(Long problemId);

    List<Testcase> findByProblemIdAndHiddenFalse(Long problemId);
}