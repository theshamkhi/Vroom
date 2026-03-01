package com.vroom.security.dto;

import com.vroom.security.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for user information response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Role role;
    private boolean enabled;
    private boolean emailVerified;
    private String profilePictureUrl;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    // Student-specific fields (null if not student)
    private Integer totalPoints;
    private Double completionPercentage;

    // Instructor-specific fields (null if not instructor)
    private String licenseNumber;
    private Integer yearsOfExperience;
    private Integer activeStudents;
}