package com.vroom.security.model.entity;

import com.vroom.security.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Student entity extending User
 * Contains student-specific fields and learning progress data
 */
@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_student_level", columnList = "current_level"),
        @Index(name = "idx_student_instructor", columnList = "assigned_instructor_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "id")
public class Student extends User {

    @Column(name = "current_level", length = 20)
    private String currentLevel;

    @Column(name = "total_points", nullable = false)
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(name = "completion_percentage", nullable = false)
    @Builder.Default
    private Double completionPercentage = 0.0;

    @Column(name = "scenarios_completed", nullable = false)
    @Builder.Default
    private Integer scenariosCompleted = 0;

    @Column(name = "badges_earned", nullable = false)
    @Builder.Default
    private Integer badgesEarned = 0;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "target_completion_date")
    private LocalDate targetCompletionDate;

    @Column(name = "assigned_instructor_id")
    private UUID assignedInstructorId;

    @Column(name = "driving_school", length = 100)
    private String drivingSchool;

    @Column(name = "permit_number", length = 50)
    private String permitNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "preferred_language", length = 10)
    @Builder.Default
    private String preferredLanguage = "en";

    @Column(name = "instructor_notes", length = 500)
    private String instructorNotes;

    @PrePersist
    protected void onStudentCreate() {
        super.onCreate();
        if (enrollmentDate == null) {
            enrollmentDate = LocalDate.now();
        }
        if (currentLevel == null) {
            currentLevel = "BEGINNER";
        }
    }
}