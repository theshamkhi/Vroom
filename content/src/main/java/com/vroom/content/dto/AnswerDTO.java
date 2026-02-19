package com.vroom.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for Answer responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {

    private UUID id;
    private String answerText;
    private Boolean isCorrect;
    private Integer orderIndex;
    private String explanation;
    private String imageUrl;
}