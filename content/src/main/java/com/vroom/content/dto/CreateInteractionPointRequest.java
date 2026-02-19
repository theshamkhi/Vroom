package com.vroom.content.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for creating a new interaction point
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInteractionPointRequest {

    @NotNull(message = "Question ID is required")
    private UUID questionId;

    @NotNull(message = "Timestamp is required")
    @Min(value = 0, message = "Timestamp cannot be negative")
    private Integer timestampSeconds;

    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Size(max = 300, message = "Description cannot exceed 300 characters")
    private String description;

    @Min(value = 0, message = "Order index cannot be negative")
    private Integer orderIndex;

    private Boolean mandatory;
}