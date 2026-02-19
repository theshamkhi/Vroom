package com.vroom.content.model.entity;

import com.vroom.content.model.enums.QuestionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Question entity representing a question at an interaction point
 */
@Entity
@Table(name = "questions", indexes = {
        @Index(name = "idx_question_scenario", columnList = "scenario_id"),
        @Index(name = "idx_question_type", columnList = "type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    /**
     * Reference to the scenario this question belongs to
     */
    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Question type is required")
    @Column(nullable = false, length = 30)
    private QuestionType type;

    @NotBlank(message = "Question text is required")
    @Size(min = 10, max = 500, message = "Question must be between 10 and 500 characters")
    @Column(nullable = false, length = 500)
    private String questionText;

    /**
     * Optional hint to help the student
     */
    @Column(length = 300)
    private String hint;

    /**
     * Explanation shown after answering (correct or incorrect)
     */
    @Column(length = 500)
    private String explanation;

    /**
     * Points awarded for correct answer
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer points = 10;

    /**
     * Time limit to answer in seconds (optional)
     */
    private Integer timeLimitSeconds;

    /**
     * Order of this question in the scenario (for sorting)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;

    /**
     * Answers for this question (bi-directional relationship)
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();

    /**
     * Statistics - how many times this question was answered
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    /**
     * Statistics - how many times answered correctly
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer correctCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void addAnswer(Answer answer) {
        if (answers == null) {
            answers = new ArrayList<>();
        }
        answers.add(answer);
        answer.setQuestion(this);
    }

    public void removeAnswer(Answer answer) {
        if (answers != null) {
            answers.remove(answer);
            answer.setQuestion(null);
        }
    }

    public void incrementAttemptCount() {
        this.attemptCount++;
    }

    public void incrementCorrectCount() {
        this.correctCount++;
    }

    public Double getSuccessRate() {
        if (attemptCount == 0) {
            return 0.0;
        }
        return (correctCount.doubleValue() / attemptCount.doubleValue()) * 100;
    }

    public boolean isTimed() {
        return timeLimitSeconds != null && timeLimitSeconds > 0;
    }

    public List<Answer> getCorrectAnswers() {
        if (answers == null) {
            return new ArrayList<>();
        }
        return answers.stream()
                .filter(Answer::getIsCorrect)
                .toList();
    }

    public int getCorrectAnswerCount() {
        return getCorrectAnswers().size();
    }
}