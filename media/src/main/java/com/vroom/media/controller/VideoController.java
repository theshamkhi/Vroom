package com.vroom.media.controller;

import com.vroom.media.dto.VideoDTO;
import com.vroom.media.dto.VideoUploadRequest;
import com.vroom.media.service.VideoService;
import com.vroom.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for video management
 */
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Videos", description = "Video upload and streaming management")
public class VideoController {

    private final VideoService videoService;

    /**
     * Upload video
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Upload video", description = "Upload a video file (Instructor/Admin only)")
    public ResponseEntity<VideoDTO> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", defaultValue = "false") Boolean isPublic) {

        try {
            // TODO: Extract actual user ID from authenticated user
            UUID uploadedBy = SecurityUtils.getCurrentUserId();

            VideoUploadRequest request = VideoUploadRequest.builder()
                    .title(title)
                    .description(description)
                    .isPublic(isPublic)
                    .build();

            VideoDTO uploaded = videoService.uploadVideo(file, request, uploadedBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploaded);

        } catch (IllegalArgumentException e) {
            log.error("Invalid video upload request", e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Failed to upload video", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all videos
     */
    @GetMapping
    @Operation(summary = "Get all videos", description = "Get all ready videos")
    public ResponseEntity<List<VideoDTO>> getAllVideos() {
        List<VideoDTO> videos = videoService.getAllVideos();
        return ResponseEntity.ok(videos);
    }

    /**
     * Get video by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get video", description = "Get video details by ID")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable UUID id) {
        try {
            VideoDTO video = videoService.getVideoById(id);
            return ResponseEntity.ok(video);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Stream video
     */
    @GetMapping("/stream/{id}")
    @Operation(summary = "Stream video", description = "Stream video content")
    public ResponseEntity<InputStreamResource> streamVideo(@PathVariable UUID id) {
        try {
            InputStream videoStream = videoService.getVideoStream(id);
            VideoDTO video = videoService.getVideoById(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(video.getMimeType()));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + video.getOriginalFilename() + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(videoStream));

        } catch (IOException e) {
            log.error("Failed to stream video", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get my uploaded videos
     */
    @GetMapping("/my-videos")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Get my videos", description = "Get videos uploaded by current user")
    public ResponseEntity<List<VideoDTO>> getMyVideos() {

        UUID uploaderId = SecurityUtils.getCurrentUserId();

        List<VideoDTO> videos = videoService.getVideosByUploader(uploaderId);
        return ResponseEntity.ok(videos);
    }

    /**
     * Delete video
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Delete video", description = "Delete video permanently")
    public ResponseEntity<Void> deleteVideo(@PathVariable UUID id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            log.error("Failed to delete video", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}