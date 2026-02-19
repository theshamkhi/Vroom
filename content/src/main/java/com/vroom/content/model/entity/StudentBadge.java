package com.vroom.content.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StudentBadge entity representing the many-to-many relationship between students and badges
 * Tracks when and how a student earned a badge
 */
@Entity
@Table(name = "student_badges", indexes = {
        @Index(name = "idx_student_badge_student", columnList = "student_id"),
        @Index(name = "idx_student_badge_badge", columnList = "badge_id"),
        @Index(name = "idx_student_badge_earned", columnList = "earnedAt")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_badge", columnNames = {"student_id", "badge_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentBadge {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    /**
     * Reference to the student who earned the badge
     */
    @NotNull(message = "Student ID is required")
    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    /**
     * Reference to the badge that was earned
     */
    @NotNull(message = "Badge ID is required")
    @Column(name = "badge_id", nullable = false)
    private UUID badgeId;

    /**
     * When the badge was earned
     */
    @NotNull(message = "Earned date is required")
    @Column(nullable = false)
    private LocalDateTime earnedAt;

    /**
     * How the badge was earned (optional context)
     * e.g., "Completed all Urban Driving scenarios", "Achieved 95% on Highway Master"
     */
    @Column(length = 300)
    private String earnedDescription;

    /**
     * Reference to the scenario where the badge was earned (if applicable)
     */
    private UUID earnedInScenarioId;

    /**
     * Whether the student has been notified about earning this badge
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean notified = false;

    /**
     * Whether the badge is displayed on the student's profile
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean displayed = true;

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (earnedAt == null) {
            earnedAt = LocalDateTime.now();
        }
    }

    // Helper methods
    public void markAsNotified() {
        this.notified = true;
    }

    public void hide() {
        this.displayed = false;
    }

    public void show() {
        this.displayed = true;
    }

    public boolean isRecent() {
        return earnedAt.isAfter(LocalDateTime.now().minusDays(7));
    }
}