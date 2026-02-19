package com.vroom.content.model.entity;

import com.vroom.content.model.enums.Difficulty;
import com.vroom.content.model.enums.Theme;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Scenario entity representing an interactive video-based driving scenario
 */
@Entity
@Table(name = "scenarios", indexes = {
        @Index(name = "idx_scenario_difficulty", columnList = "difficulty"),
        @Index(name = "idx_scenario_theme", columnList = "theme"),
        @Index(name = "idx_scenario_published", columnList = "published")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scenario {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Difficulty is required")
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Theme is required")
    @Column(nullable = false, length = 50)
    private Theme theme;

    /**
     * Reference to the video ID from the media module
     */
    @Column(name = "video_id")
    private UUID videoId;

    /**
     * Duration of the video in seconds
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer durationSeconds = 0;

    /**
     * Estimated time to complete including questions (in minutes)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer estimatedMinutes = 5;

    /**
     * Tags for better categorization and search
     */
    @ElementCollection
    @CollectionTable(name = "scenario_tags", joinColumns = @JoinColumn(name = "scenario_id"))
    @Column(name = "tag")
    @Builder.Default
    private Set<String> tags = new HashSet<>();

    /**
     * Prerequisites - other scenarios that should be completed first
     */
    @ElementCollection
    @CollectionTable(name = "scenario_prerequisites", joinColumns = @JoinColumn(name = "scenario_id"))
    @Column(name = "prerequisite_id")
    @Builder.Default
    private Set<UUID> prerequisiteIds = new HashSet<>();

    /**
     * Thumbnail URL or path
     */
    @Column(length = 500)
    private String thumbnailUrl;

    /**
     * Learning objectives for this scenario
     */
    @ElementCollection
    @CollectionTable(name = "scenario_objectives", joinColumns = @JoinColumn(name = "scenario_id"))
    @Column(name = "objective", length = 200)
    @Builder.Default
    private Set<String> learningObjectives = new HashSet<>();

    /**
     * Maximum points that can be earned in this scenario
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer maxPoints = 100;

    /**
     * Minimum score percentage to pass (0-100)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer passingScore = 70;

    /**
     * Whether this scenario is published and visible to students
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean published = false;

    /**
     * Number of times this scenario has been completed
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer completionCount = 0;

    /**
     * Average score achieved by all students (0-100)
     */
    @Column
    private Double averageScore;

    /**
     * Average time taken to complete (in minutes)
     */
    @Column
    private Double averageCompletionTime;

    /**
     * Creator of the scenario (instructor or admin ID)
     */
    @Column(nullable = false)
    private UUID createdBy;

    /**
     * Last person who modified the scenario
     */
    private UUID lastModifiedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;

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
    public void publish(UUID publishedBy) {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
        this.lastModifiedBy = publishedBy;
    }

    public void unpublish(UUID unpublishedBy) {
        this.published = false;
        this.lastModifiedBy = unpublishedBy;
    }

    public void incrementCompletionCount() {
        this.completionCount++;
    }

    public void updateStatistics(Double newScore, Double newCompletionTime) {
        // Update average score
        if (averageScore == null) {
            averageScore = newScore;
        } else {
            averageScore = ((averageScore * (completionCount - 1)) + newScore) / completionCount;
        }

        // Update average completion time
        if (averageCompletionTime == null) {
            averageCompletionTime = newCompletionTime;
        } else {
            averageCompletionTime = ((averageCompletionTime * (completionCount - 1)) + newCompletionTime) / completionCount;
        }
    }

    public boolean isPassingScore(Double score) {
        return score >= passingScore;
    }

    public void addTag(String tag) {
        if (tags == null) {
            tags = new HashSet<>();
        }
        tags.add(tag.toLowerCase().trim());
    }

    public void removeTag(String tag) {
        if (tags != null) {
            tags.remove(tag.toLowerCase().trim());
        }
    }

    public void addPrerequisite(UUID prerequisiteId) {
        if (prerequisiteIds == null) {
            prerequisiteIds = new HashSet<>();
        }
        prerequisiteIds.add(prerequisiteId);
    }

    public void addLearningObjective(String objective) {
        if (learningObjectives == null) {
            learningObjectives = new HashSet<>();
        }
        learningObjectives.add(objective);
    }
}