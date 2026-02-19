package com.vroom.content.dto;

import com.vroom.content.model.enums.Difficulty;
import com.vroom.content.model.enums.Theme;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * DTO for creating a new scenario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScenarioRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotNull(message = "Theme is required")
    private Theme theme;

    private UUID videoId;

    @Min(value = 1, message = "Duration must be at least 1 second")
    private Integer durationSeconds;

    @Min(value = 1, message = "Estimated time must be at least 1 minute")
    private Integer estimatedMinutes;

    private Set<String> tags;
    private Set<UUID> prerequisiteIds;
    private String thumbnailUrl;
    private Set<String> learningObjectives;

    @Min(value = 1, message = "Max points must be at least 1")
    private Integer maxPoints;

    @Min(value = 0, message = "Passing score cannot be negative")
    @Max(value = 100, message = "Passing score cannot exceed 100")
    private Integer passingScore;
}