package com.vroom.content.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Answer entity representing a possible answer to a question
 */
@Entity
@Table(name = "answers", indexes = {
        @Index(name = "idx_answer_question", columnList = "question_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    /**
     * The question this answer belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @NotBlank(message = "Answer text is required")
    @Size(min = 1, max = 300, message = "Answer must be between 1 and 300 characters")
    @Column(nullable = false, length = 300)
    private String answerText;

    /**
     * Whether this answer is correct
     */
    @NotNull(message = "Correct flag is required")
    @Column(nullable = false)
    private Boolean isCorrect;

    /**
     * Order of this answer (for display purposes)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;

    /**
     * Optional explanation for why this answer is correct/incorrect
     */
    @Column(length = 500)
    private String explanation;

    /**
     * Optional image URL for visual answers
     */
    @Column(length = 500)
    private String imageUrl;

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
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.isBlank();
    }

    public boolean hasExplanation() {
        return explanation != null && !explanation.isBlank();
    }
}