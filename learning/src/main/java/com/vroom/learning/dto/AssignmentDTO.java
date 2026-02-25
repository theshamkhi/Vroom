package com.vroom.learning.dto;

import com.vroom.learning.model.enums.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for assignments
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {

    private UUID id;
    private String title;
    private String description;
    private UUID instructorId;
    private String instructorName;
    private UUID studentId;
    private String studentName;
    private Set<UUID> scenarioIds;
    private Integer scenarioCount;
    private AssignmentStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime gradedAt;
    private Double grade;
    private String instructorFeedback;
    private String studentNotes;
    private LocalDateTime createdAt;
    private Boolean isOverdue;
}