package com.vroom.learning.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity tracking student's answer to a specific question
 */
@Entity
@Table(name = "student_answers", indexes = {
        @Index(name = "idx_student_answer_student", columnList = "student_id"),
        @Index(name = "idx_student_answer_question", columnList = "question_id"),
        @Index(name = "idx_student_answer_scenario", columnList = "scenario_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Student ID is required")
    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @NotNull(message = "Question ID is required")
    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @NotNull(message = "Scenario ID is required")
    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    /**
     * Student scenario progress reference
     */
    @Column(name = "student_scenario_id")
    private UUID studentScenarioId;

    /**
     * Selected answer IDs (for multiple choice, can be multiple)
     */
    @ElementCollection
    @CollectionTable(name = "student_answer_selections", joinColumns = @JoinColumn(name = "student_answer_id"))
    @Column(name = "answer_id")
    @Builder.Default
    private Set<UUID> selectedAnswerIds = new HashSet<>();

    /**
     * Whether the answer was correct
     */
    @NotNull(message = "Correct flag is required")
    @Column(nullable = false)
    private Boolean isCorrect;

    /**
     * Points earned for this answer
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer pointsEarned = 0;

    /**
     * Time taken to answer (in seconds)
     */
    private Integer timeTakenSeconds;

    /**
     * Whether a hint was used
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean hintUsed = false;

    /**
     * Attempt number (for retries)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer attemptNumber = 1;

    @Column(nullable = false, updatable = false)
    private LocalDateTime answeredAt;

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        answeredAt = LocalDateTime.now();
    }

    // Helper methods
    public void addSelectedAnswer(UUID answerId) {
        if (selectedAnswerIds == null) {
            selectedAnswerIds = new HashSet<>();
        }
        selectedAnswerIds.add(answerId);
    }

    public boolean hasMultipleSelections() {
        return selectedAnswerIds != null && selectedAnswerIds.size() > 1;
    }
}