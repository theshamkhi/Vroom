package com.vroom.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorStudentDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    private String currentLevel;
    private Integer totalPoints;
    private Double completionPercentage;
    private Integer scenariosCompleted;
    private Integer badgesEarned;

    private LocalDate enrollmentDate;
    private LocalDate targetCompletionDate;

    private UUID assignedInstructorId;
    private String drivingSchool;

    private String instructorNotes;
}
