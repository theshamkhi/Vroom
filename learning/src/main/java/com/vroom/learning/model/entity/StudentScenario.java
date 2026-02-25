package com.vroom.learning.model.entity;

import com.vroom.learning.model.enums.CompletionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity tracking student progress on a specific scenario
 */
@Entity
@Table(name = "student_scenarios", indexes = {
        @Index(name = "idx_student_scenario_student", columnList = "student_id"),
        @Index(name = "idx_student_scenario_scenario", columnList = "scenario_id"),
        @Index(name = "idx_student_scenario_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_scenario", columnNames = {"student_id", "scenario_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentScenario {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Student ID is required")
    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @NotNull(message = "Scenario ID is required")
    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(nullable = false, length = 30)
    @Builder.Default
    private CompletionStatus status = CompletionStatus.NOT_STARTED;

    /**
     * Number of attempts made
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    /**
     * Highest score achieved (0-100)
     */
    @Min(0)
    @Max(100)
    private Double highestScore;

    /**
     * Latest score (0-100)
     */
    @Min(0)
    @Max(100)
    private Double latestScore;

    /**
     * Average score across all attempts
     */
    private Double averageScore;

    /**
     * Total points earned
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer totalPointsEarned = 0;

    /**
     * Total possible points in this scenario
     */
    private Integer totalPossiblePoints;

    /**
     * Time spent on scenario (in seconds)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    /**
     * Number of questions answered correctly
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer correctAnswers = 0;

    /**
     * Total number of questions in scenario
     */
    private Integer totalQuestions;

    /**
     * When the student first started this scenario
     */
    private LocalDateTime startedAt;

    /**
     * When the scenario was completed (first completion)
     */
    private LocalDateTime completedAt;

    /**
     * Last time the student accessed this scenario
     */
    private LocalDateTime lastAccessedAt;

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
    public void startScenario() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        status = CompletionStatus.IN_PROGRESS;
        lastAccessedAt = LocalDateTime.now();
    }

    public void completeScenario(Double score, Integer pointsEarned, Integer correctAnswers, Integer totalQuestions) {
        this.attemptCount++;
        this.latestScore = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.totalPointsEarned += pointsEarned;

        // Update highest score
        if (highestScore == null || score > highestScore) {
            highestScore = score;
        }

        // Calculate average score
        if (averageScore == null) {
            averageScore = score;
        } else {
            averageScore = ((averageScore * (attemptCount - 1)) + score) / attemptCount;
        }

        // Determine pass/fail (assuming 70% is passing)
        if (score >= 70.0) {
            status = CompletionStatus.COMPLETED_PASSED;
            if (completedAt == null) {
                completedAt = LocalDateTime.now();
            }
        } else {
            status = CompletionStatus.COMPLETED_FAILED;
        }

        lastAccessedAt = LocalDateTime.now();
    }

    public void addTimeSpent(Integer seconds) {
        this.timeSpentSeconds += seconds;
    }

    public boolean isPassed() {
        return status == CompletionStatus.COMPLETED_PASSED;
    }

    public boolean isCompleted() {
        return status.isCompleted();
    }

    public Integer getCompletionPercentage() {
        if (totalQuestions == null || totalQuestions == 0) {
            return 0;
        }
        return (correctAnswers * 100) / totalQuestions;
    }

    public String getFormattedTimeSpent() {
        int hours = timeSpentSeconds / 3600;
        int minutes = (timeSpentSeconds % 3600) / 60;
        int seconds = timeSpentSeconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
}