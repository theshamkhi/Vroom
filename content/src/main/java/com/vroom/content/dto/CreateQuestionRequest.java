package com.vroom.content.dto;

import com.vroom.content.model.enums.QuestionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating a new question
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {

    @NotNull(message = "Question type is required")
    private QuestionType type;

    @NotBlank(message = "Question text is required")
    @Size(min = 10, max = 500, message = "Question must be between 10 and 500 characters")
    private String questionText;

    @Size(max = 300, message = "Hint cannot exceed 300 characters")
    private String hint;

    @Size(max = 500, message = "Explanation cannot exceed 500 characters")
    private String explanation;

    @Min(value = 1, message = "Points must be at least 1")
    private Integer points;

    @Min(value = 5, message = "Time limit must be at least 5 seconds")
    private Integer timeLimitSeconds;

    @Min(value = 0, message = "Order index cannot be negative")
    private Integer orderIndex;

    @NotNull(message = "At least one answer is required")
    @Size(min = 1, message = "At least one answer is required")
    private List<CreateAnswerRequest> answers;
}