package com.example.codeforge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "testcases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Testcase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
    
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String input;
    
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String expectedOutput;
    
    @Column(nullable = false)
    private Boolean isHidden = false; // Hidden testcases for verification
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}