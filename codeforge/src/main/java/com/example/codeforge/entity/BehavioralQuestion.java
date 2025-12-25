package com.example.codeforge.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "behavioral_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BehavioralQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BehavioralCategory category;


    @Column(nullable = false)
    private Boolean isActive = true;
}