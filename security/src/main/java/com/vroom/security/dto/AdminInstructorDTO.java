package com.vroom.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInstructorDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private String licenseNumber;
    private String drivingSchool;
    private Integer yearsOfExperience;
    private LocalDate joinDate;

    private BigDecimal averageRating;
    private Integer totalRatings;
    private Integer activeStudents;
    private Integer totalStudentsTaught;
    private Boolean availableForNewStudents;
    private Integer maxStudents;
}
