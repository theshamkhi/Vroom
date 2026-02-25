package com.vroom.learning.dto;

import com.vroom.learning.model.enums.CompletionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for student progress on a scenario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDTO {

    private UUID id;
    private UUID studentId;
    private UUID scenarioId;
    private String scenarioTitle;
    private CompletionStatus status;
    private Integer attemptCount;
    private Double highestScore;
    private Double latestScore;
    private Double averageScore;
    private Integer totalPointsEarned;
    private Integer totalPossiblePoints;
    private Integer timeSpentSeconds;
    private String formattedTimeSpent;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Integer completionPercentage;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
}