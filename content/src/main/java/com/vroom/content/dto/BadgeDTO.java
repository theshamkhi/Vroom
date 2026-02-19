package com.vroom.content.dto;

import com.vroom.content.model.enums.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Badge responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {

    private UUID id;
    private String name;
    private String description;
    private BadgeType type;
    private String iconUrl;
    private String criteria;
    private Integer pointsValue;
    private Boolean active;
    private Integer earnedCount;
    private UUID relatedScenarioId;
    private String relatedTheme;
    private LocalDateTime createdAt;

    // For student badge view
    private LocalDateTime earnedAt;
    private String earnedDescription;
    private Boolean isEarned;
}