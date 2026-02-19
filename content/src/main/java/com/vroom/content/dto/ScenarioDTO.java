package com.vroom.content.dto;

import com.vroom.content.model.enums.Difficulty;
import com.vroom.content.model.enums.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for Scenario responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioDTO {

    private UUID id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private Theme theme;
    private UUID videoId;
    private Integer durationSeconds;
    private Integer estimatedMinutes;
    private Set<String> tags;
    private Set<UUID> prerequisiteIds;
    private String thumbnailUrl;
    private Set<String> learningObjectives;
    private Integer maxPoints;
    private Integer passingScore;
    private Boolean published;
    private Integer completionCount;
    private Double averageScore;
    private Double averageCompletionTime;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    // Summary fields
    private Integer questionCount;
    private Integer interactionPointCount;
}