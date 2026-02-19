package com.vroom.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new answer
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnswerRequest {

    @NotBlank(message = "Answer text is required")
    @Size(min = 1, max = 300, message = "Answer must be between 1 and 300 characters")
    private String answerText;

    @NotNull(message = "Correct flag is required")
    private Boolean isCorrect;

    private Integer orderIndex;

    @Size(max = 500, message = "Explanation cannot exceed 500 characters")
    private String explanation;

    private String imageUrl;
}