package com.vroom.content.dto;

import com.vroom.content.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Question responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    private UUID id;
    private UUID scenarioId;
    private QuestionType type;
    private String questionText;
    private String hint;
    private String explanation;
    private Integer points;
    private Integer timeLimitSeconds;
    private Integer orderIndex;
    private List<AnswerDTO> answers;
    private Integer attemptCount;
    private Integer correctCount;
    private Double successRate;
    private LocalDateTime createdAt;
}