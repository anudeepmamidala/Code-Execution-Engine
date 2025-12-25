package com.example.codeforge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "behavioral_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BehavioralAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private BehavioralQuestion question;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String answerText;

    @Column(nullable = false)
    private Integer wordCount;

    @Column(nullable = false)
    private Integer starScore;

    @Column(nullable = false, length = 200)
    private String feedback;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        wordCount = (answerText == null || answerText.trim().isEmpty())
                ? 0
                : answerText.trim().split("\\s+").length;

        if (starScore == null) starScore = 0;
        if (feedback == null) feedback = "Not evaluated yet";
}

}