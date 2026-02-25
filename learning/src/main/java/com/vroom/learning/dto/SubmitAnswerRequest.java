package com.vroom.learning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * DTO for submitting an answer to a question
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {

    @NotNull(message = "Question ID is required")
    private UUID questionId;

    @NotNull(message = "Scenario ID is required")
    private UUID scenarioId;

    @NotNull(message = "Selected answer IDs are required")
    private Set<UUID> selectedAnswerIds;

    private Integer timeTakenSeconds;
    private Boolean hintUsed;
}