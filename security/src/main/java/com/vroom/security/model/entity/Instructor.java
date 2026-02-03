package com.vroom.security.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Instructor entity extending User
 * Contains instructor-specific fields and teaching data
 */
@Entity
@Table(name = "instructors", indexes = {
        @Index(name = "idx_instructor_school", columnList = "driving_school"),
        @Index(name = "idx_instructor_specialty", columnList = "specialty")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "id")
public class Instructor extends User {

    @Column(name = "license_number", unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "driving_school", length = 100)
    private String drivingSchool;

    @Column(name = "specialty", length = 100)
    private String specialty;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "average_rating", precision = 2, scale = 1)
    private BigDecimal averageRating;

    @Column(name = "total_ratings", nullable = false)
    @Builder.Default
    private Integer totalRatings = 0;

    @Column(name = "active_students", nullable = false)
    @Builder.Default
    private Integer activeStudents = 0;

    @Column(name = "total_students_taught", nullable = false)
    @Builder.Default
    private Integer totalStudentsTaught = 0;

    @Column(name = "available_for_new_students", nullable = false)
    @Builder.Default
    private Boolean availableForNewStudents = true;

    @Column(name = "max_students", nullable = false)
    @Builder.Default
    private Integer maxStudents = 20;

    @Column(name = "availability", length = 200)
    private String availability;

    @Column(name = "languages_spoken", length = 100)
    private String languagesSpoken;

    @Column(name = "certifications", length = 300)
    private String certifications;

    @PrePersist
    protected void onInstructorCreate() {
        super.onCreate();
        if (joinDate == null) {
            joinDate = LocalDate.now();
        }
    }
}