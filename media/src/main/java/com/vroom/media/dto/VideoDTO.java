package com.vroom.media.dto;

import com.vroom.media.model.enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Video responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {

    private UUID id;
    private String title;
    private String description;
    private String originalFilename;
    private String mimeType;
    private Long fileSizeBytes;
    private String formattedFileSize;
    private Integer durationSeconds;
    private String formattedDuration;
    private String resolution;
    private String videoUrl;
    private String thumbnailUrl;
    private VideoStatus status;
    private String storageType;
    private UUID uploadedBy;
    private Long viewCount;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}