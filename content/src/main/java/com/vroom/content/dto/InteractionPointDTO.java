package com.vroom.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for InteractionPoint responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionPointDTO {

    private UUID id;
    private UUID scenarioId;
    private UUID questionId;
    private Integer timestampSeconds;
    private String timestampFormatted; // "MM:SS" format
    private String title;
    private String description;
    private Integer orderIndex;
    private Boolean mandatory;

    // Nested question
    private QuestionDTO question;
}