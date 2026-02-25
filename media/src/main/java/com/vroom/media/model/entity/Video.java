package com.vroom.media.model.entity;

import com.vroom.media.model.enums.VideoStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Video entity representing uploaded video files
 */
@Entity
@Table(name = "videos", indexes = {
        @Index(name = "idx_video_status", columnList = "status"),
        @Index(name = "idx_video_uploaded_by", columnList = "uploadedBy")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    /**
     * Original filename when uploaded
     */
    @NotBlank(message = "Filename is required")
    @Column(nullable = false, length = 255)
    private String originalFilename;

    /**
     * Stored filename (UUID-based)
     */
    @NotBlank(message = "Stored filename is required")
    @Column(nullable = false, length = 255)
    private String storedFilename;

    /**
     * File path (local) or S3 key (cloud)
     */
    @NotBlank(message = "File path is required")
    @Column(nullable = false, length = 500)
    private String filePath;

    /**
     * MIME type (e.g., video/mp4)
     */
    @Column(length = 100)
    private String mimeType;

    /**
     * File size in bytes
     */
    @Column(nullable = false)
    private Long fileSizeBytes;

    /**
     * Video duration in seconds
     */
    private Integer durationSeconds;

    /**
     * Video resolution (e.g., "1920x1080")
     */
    @Column(length = 20)
    private String resolution;

    /**
     * Thumbnail filename
     */
    @Column(length = 255)
    private String thumbnailFilename;

    /**
     * Thumbnail path
     */
    @Column(length = 500)
    private String thumbnailPath;

    /**
     * URL for accessing the video (local or S3 presigned URL)
     */
    @Column(length = 1000)
    private String videoUrl;

    /**
     * URL for accessing the thumbnail
     */
    @Column(length = 1000)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VideoStatus status = VideoStatus.UPLOADING;

    /**
     * Storage location: LOCAL or S3
     */
    @Column(length = 20)
    @Builder.Default
    private String storageType = "LOCAL";

    /**
     * User who uploaded the video
     */
    @NotNull(message = "Uploader is required")
    @Column(nullable = false)
    private UUID uploadedBy;

    /**
     * Number of times this video has been viewed
     */
    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    /**
     * Whether the video is publicly accessible
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime processedAt;

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
    public void markAsProcessing() {
        this.status = VideoStatus.PROCESSING;
    }

    public void markAsReady() {
        this.status = VideoStatus.READY;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = VideoStatus.FAILED;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public String getFormattedDuration() {
        if (durationSeconds == null) {
            return "00:00";
        }
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getFormattedFileSize() {
        if (fileSizeBytes == null) {
            return "0 B";
        }

        if (fileSizeBytes < 1024) {
            return fileSizeBytes + " B";
        } else if (fileSizeBytes < 1024 * 1024) {
            return String.format("%.2f KB", fileSizeBytes / 1024.0);
        } else if (fileSizeBytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", fileSizeBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", fileSizeBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public boolean isReady() {
        return status == VideoStatus.READY;
    }

    public boolean isFailed() {
        return status == VideoStatus.FAILED;
    }
}