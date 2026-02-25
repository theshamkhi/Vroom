package com.vroom.media.service;

import com.vroom.media.dto.VideoDTO;
import com.vroom.media.dto.VideoUploadRequest;
import com.vroom.media.model.entity.Video;
import com.vroom.media.model.enums.VideoStatus;
import com.vroom.media.repository.VideoRepository;
import com.vroom.media.service.storage.VideoStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * Service for video management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoStorageService videoStorageService;
    private final ThumbnailService thumbnailService;

    /**
     * Upload video file
     */
    @Transactional
    public VideoDTO uploadVideo(MultipartFile file, VideoUploadRequest request, UUID uploadedBy) throws IOException {
        log.info("Uploading video: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());

        // Validate file
        validateVideoFile(file);

        // Create video entity
        UUID videoId = UUID.randomUUID();
        String storedFilename = videoId.toString() + getFileExtension(file.getOriginalFilename());

        Video video = Video.builder()
                .id(videoId)
                .title(request.getTitle())
                .description(request.getDescription())
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedFilename)
                .mimeType(file.getContentType())
                .fileSizeBytes(file.getSize())
                .status(VideoStatus.UPLOADING)
                .storageType(videoStorageService.getStorageType())
                .uploadedBy(uploadedBy)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .build();

        // Save to database first
        video = videoRepository.save(video);

        try {
            // Upload to storage
            String filePath = videoStorageService.uploadVideo(file, videoId);
            video.setFilePath(filePath);
            video.markAsProcessing();

            // Generate thumbnail asynchronously
            try {
                byte[] thumbnailData = thumbnailService.generateThumbnail(file);
                String thumbnailPath = videoStorageService.uploadThumbnail(thumbnailData, videoId);
                video.setThumbnailPath(thumbnailPath);
                video.setThumbnailFilename(videoId.toString() + ".jpg");
            } catch (Exception e) {
                log.error("Failed to generate thumbnail", e);
                // Continue without thumbnail
            }

            // Mark as ready
            video.setVideoUrl(videoStorageService.getVideoUrl(filePath));
            video.setThumbnailUrl(video.getThumbnailPath() != null ?
                    videoStorageService.getThumbnailUrl(video.getThumbnailPath()) : null);
            video.markAsReady();

            video = videoRepository.save(video);

            log.info("Video uploaded successfully: {}", videoId);
            return mapToDTO(video);

        } catch (Exception e) {
            log.error("Failed to upload video", e);
            video.markAsFailed();
            videoRepository.save(video);
            throw new IOException("Failed to upload video", e);
        }
    }

    /**
     * Get video by ID
     */
    public VideoDTO getVideoById(UUID id) {
        log.debug("Fetching video: {}", id);

        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));

        return mapToDTO(video);
    }

    /**
     * Get video stream
     */
    public InputStream getVideoStream(UUID id) throws IOException {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));

        if (!video.isReady()) {
            throw new RuntimeException("Video is not ready for streaming");
        }

        // Increment view count
        video.incrementViewCount();
        videoRepository.save(video);

        return videoStorageService.getVideoStream(video.getFilePath());
    }

    /**
     * Get all videos
     */
    public List<VideoDTO> getAllVideos() {
        return videoRepository.findByStatusOrderByCreatedAtDesc(VideoStatus.READY)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get videos by uploader
     */
    public List<VideoDTO> getVideosByUploader(UUID uploaderId) {
        return videoRepository.findByUploadedByOrderByCreatedAtDesc(uploaderId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Delete video
     */
    @Transactional
    public void deleteVideo(UUID id) throws IOException {
        log.info("Deleting video: {}", id);

        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));

        // Delete from storage
        try {
            videoStorageService.deleteVideo(video.getFilePath());
            if (video.getThumbnailPath() != null) {
                videoStorageService.deleteVideo(video.getThumbnailPath());
            }
        } catch (Exception e) {
            log.error("Failed to delete video from storage", e);
        }

        // Delete from database
        videoRepository.delete(video);
        log.info("Video deleted successfully: {}", id);
    }

    /**
     * Validate video file
     */
    private void validateVideoFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("File must be a video");
        }

        // Max file size: 500MB (configurable)
        long maxSize = 500 * 1024 * 1024; // 500MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size (500MB)");
        }
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".mp4";
    }

    /**
     * Map entity to DTO
     */
    private VideoDTO mapToDTO(Video video) {
        return VideoDTO.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .originalFilename(video.getOriginalFilename())
                .mimeType(video.getMimeType())
                .fileSizeBytes(video.getFileSizeBytes())
                .formattedFileSize(video.getFormattedFileSize())
                .durationSeconds(video.getDurationSeconds())
                .formattedDuration(video.getFormattedDuration())
                .resolution(video.getResolution())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .status(video.getStatus())
                .storageType(video.getStorageType())
                .uploadedBy(video.getUploadedBy())
                .viewCount(video.getViewCount())
                .isPublic(video.getIsPublic())
                .createdAt(video.getCreatedAt())
                .processedAt(video.getProcessedAt())
                .build();
    }
}