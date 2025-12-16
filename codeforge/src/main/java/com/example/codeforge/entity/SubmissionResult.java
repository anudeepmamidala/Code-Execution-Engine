package com.example.codeforge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submission_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testcase_id", nullable = false)
    private Testcase testcase;
    
    @Column(nullable = false)
    private Boolean passed;
    
    @Column(columnDefinition = "LONGTEXT")
    private String output;
    
    @Column(columnDefinition = "LONGTEXT")
    private String error;
    
    @Column(name = "execution_time")
    private Integer executionTime; // milliseconds
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}