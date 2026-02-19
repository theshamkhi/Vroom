package com.vroom.content.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * InteractionPoint entity representing a timestamp where the video pauses for a question
 */
@Entity
@Table(name = "interaction_points", indexes = {
        @Index(name = "idx_interaction_scenario", columnList = "scenario_id"),
        @Index(name = "idx_interaction_timestamp", columnList = "timestampSeconds")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionPoint {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    /**
     * Reference to the scenario this interaction point belongs to
     */
    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    /**
     * Reference to the question displayed at this interaction point
     */
    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    /**
     * Timestamp in seconds when the video should pause
     * e.g., 45 for 00:45, 90 for 01:30
     */
    @NotNull(message = "Timestamp is required")
    @Column(nullable = false)
    private Integer timestampSeconds;

    /**
     * Optional title/name for this interaction point
     */
    @Column(length = 100)
    private String title;

    /**
     * Optional description of what's happening at this point
     */
    @Column(length = 300)
    private String description;

    /**
     * Order of this interaction point in the scenario
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;

    /**
     * Whether this interaction point is mandatory or optional
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean mandatory = true;

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
    public String getTimestampFormatted() {
        int minutes = timestampSeconds / 60;
        int seconds = timestampSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public boolean isOptional() {
        return !mandatory;
    }

    public void setTimestampFromFormatted(String formatted) {
        // Parse format like "01:30" to 90 seconds
        String[] parts = formatted.split(":");
        if (parts.length == 2) {
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            this.timestampSeconds = (minutes * 60) + seconds;
        }
    }
}