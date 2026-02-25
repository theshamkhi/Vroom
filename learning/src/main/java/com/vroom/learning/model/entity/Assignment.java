package com.vroom.learning.model.entity;

import com.vroom.learning.model.enums.AssignmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing assignments created by instructors for students
 */
@Entity
@Table(name = "assignments", indexes = {
        @Index(name = "idx_assignment_instructor", columnList = "instructor_id"),
        @Index(name = "idx_assignment_student", columnList = "student_id"),
        @Index(name = "idx_assignment_status", columnList = "status"),
        @Index(name = "idx_assignment_due_date", columnList = "dueDate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Instructor ID is required")
    @Column(name = "instructor_id", nullable = false)
    private UUID instructorId;

    @NotNull(message = "Student ID is required")
    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    /**
     * Scenarios to complete for this assignment
     */
    @ElementCollection
    @CollectionTable(name = "assignment_scenarios", joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "scenario_id")
    @Builder.Default
    private Set<UUID> scenarioIds = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.PENDING;

    /**
     * Due date for completion
     */
    private LocalDateTime dueDate;

    /**
     * When the student started working on it
     */
    private LocalDateTime startedAt;

    /**
     * When the student completed it
     */
    private LocalDateTime completedAt;

    /**
     * When the instructor graded it
     */
    private LocalDateTime gradedAt;

    /**
     * Instructor's grade/score (0-100)
     */
    private Double grade;

    /**
     * Instructor's feedback/comments
     */
    @Column(length = 1000)
    private String instructorFeedback;

    /**
     * Student's notes/comments
     */
    @Column(length = 500)
    private String studentNotes;

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
    public void start() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        status = AssignmentStatus.IN_PROGRESS;
    }

    public void complete() {
        completedAt = LocalDateTime.now();
        status = AssignmentStatus.COMPLETED;
    }

    public void grade(Double gradeValue, String feedback) {
        this.grade = gradeValue;
        this.instructorFeedback = feedback;
        this.gradedAt = LocalDateTime.now();
        this.status = AssignmentStatus.GRADED;
    }

    public boolean isOverdue() {
        return dueDate != null &&
                LocalDateTime.now().isAfter(dueDate) &&
                status != AssignmentStatus.COMPLETED &&
                status != AssignmentStatus.GRADED;
    }

    public void checkAndMarkOverdue() {
        if (isOverdue() && status == AssignmentStatus.PENDING) {
            status = AssignmentStatus.OVERDUE;
        }
    }

    public void addScenario(UUID scenarioId) {
        if (scenarioIds == null) {
            scenarioIds = new HashSet<>();
        }
        scenarioIds.add(scenarioId);
    }

    public Integer getScenarioCount() {
        return scenarioIds != null ? scenarioIds.size() : 0;
    }
}