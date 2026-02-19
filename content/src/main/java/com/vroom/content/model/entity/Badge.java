package com.vroom.content.model.entity;

import com.vroom.content.model.enums.BadgeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Badge entity representing achievements that students can earn
 */
@Entity
@Table(name = "badges", indexes = {
        @Index(name = "idx_badge_type", columnList = "type"),
        @Index(name = "idx_badge_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Badge name is required")
    @Size(min = 3, max = 100, message = "Badge name must be between 3 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Badge description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Badge type is required")
    @Column(nullable = false, length = 30)
    private BadgeType type;

    /**
     * Icon URL or emoji for the badge
     */
    @Column(length = 500)
    private String iconUrl;

    /**
     * Criteria to earn this badge (JSON or text description)
     */
    @Column(length = 1000)
    private String criteria;

    /**
     * Points awarded when badge is earned
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer pointsValue = 50;

    /**
     * Whether this badge is currently active/available
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Number of times this badge has been earned
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer earnedCount = 0;

    /**
     * Related scenario ID (for theme-specific badges)
     */
    private UUID relatedScenarioId;

    /**
     * Related theme (for theme master badges)
     */
    @Column(length = 50)
    private String relatedTheme;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void incrementEarnedCount() {
        this.earnedCount++;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isThemeBadge() {
        return relatedTheme != null && !relatedTheme.isBlank();
    }

    public boolean isScenarioBadge() {
        return relatedScenarioId != null;
    }
}