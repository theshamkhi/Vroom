package com.vroom.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private UUID studentId;
    private String studentName;
    private String studentEmail;
    private UUID scenarioId;
    private String scenarioTitle;
    private LocalDateTime assignedAt;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private String note;
    private String status;
}