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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public ResponseEntity<Resource> streamVideo(
            @PathVariable UUID id,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader) {

        try {
            VideoDTO video = videoService.getVideoById(id);

            long fileSize = video.getFileSizeBytes() != null ? video.getFileSizeBytes() : -1L;
            if (fileSize <= 0) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Video file size is unknown");
            }

            long rangeStart = 0;
            long rangeEnd = fileSize - 1;
            boolean isRangeRequest = rangeHeader != null && rangeHeader.startsWith("bytes=");

            if (isRangeRequest) {
                Pattern pattern = Pattern.compile("bytes=(\\d*)-(\\d*)");
                Matcher matcher = pattern.matcher(rangeHeader);
                if (!matcher.matches()) {
                    throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "Invalid Range header");
                }

                String startGroup = matcher.group(1);
                String endGroup = matcher.group(2);

                if (startGroup != null && !startGroup.isEmpty()) {
                    rangeStart = Long.parseLong(startGroup);
                }
                if (endGroup != null && !endGroup.isEmpty()) {
                    rangeEnd = Long.parseLong(endGroup);
                }

                if (startGroup == null || startGroup.isEmpty()) {
                    // Suffix range: bytes=-N (last N bytes)
                    if (endGroup == null || endGroup.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "Invalid Range header");
                    }
                    long suffixLength = Long.parseLong(endGroup);
                    if (suffixLength <= 0) {
                        throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "Invalid Range header");
                    }
                    rangeStart = Math.max(0, fileSize - suffixLength);
                    rangeEnd = fileSize - 1;
                } else if (endGroup == null || endGroup.isEmpty()) {
                    // Open ended: bytes=N-
                    rangeEnd = fileSize - 1;
                }

                if (rangeStart < 0 || rangeStart >= fileSize || rangeEnd < rangeStart) {
                    throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "Range not satisfiable");
                }

                rangeEnd = Math.min(rangeEnd, fileSize - 1);
            }

            InputStream videoStream = videoService.getVideoStream(id);
            if (isRangeRequest) {
                skipFully(videoStream, rangeStart);
            }

            long contentLength = (rangeEnd - rangeStart) + 1;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(video.getMimeType()));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + video.getOriginalFilename() + "\"");
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.setContentLength(contentLength);

            if (isRangeRequest) {
                headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize);
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .body(new InputStreamResource(new LimitedInputStream(videoStream, contentLength)));
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(videoStream));

        } catch (IOException e) {
            log.error("Failed to stream video", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static void skipFully(InputStream inputStream, long bytesToSkip) throws IOException {
        long remaining = bytesToSkip;
        while (remaining > 0) {
            long skipped = inputStream.skip(remaining);
            if (skipped <= 0) {
                int read = inputStream.read();
                if (read == -1) {
                    throw new IOException("Unexpected end of stream while skipping");
                }
                skipped = 1;
            }
            remaining -= skipped;
        }
    }

    private static class LimitedInputStream extends FilterInputStream {
        private long remaining;

        protected LimitedInputStream(InputStream in, long limit) {
            super(in);
            this.remaining = limit;
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int result = super.read();
            if (result != -1) {
                remaining--;
            }
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int toRead = (int) Math.min(len, remaining);
            int count = super.read(b, off, toRead);
            if (count != -1) {
                remaining -= count;
            }
            return count;
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