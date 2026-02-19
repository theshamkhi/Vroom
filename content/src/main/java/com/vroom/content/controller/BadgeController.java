package com.vroom.content.controller;

import com.vroom.content.dto.BadgeDTO;
import com.vroom.content.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for badge management
 */
@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Badges", description = "Achievement badge management")
public class BadgeController {

    private final BadgeService badgeService;

    /**
     * Get all active badges
     */
    @GetMapping
    @Operation(summary = "Get all badges", description = "Get all active badges available")
    public ResponseEntity<List<BadgeDTO>> getAllBadges() {
        List<BadgeDTO> badges = badgeService.getAllActiveBadges();
        return ResponseEntity.ok(badges);
    }

    /**
     * Get badge by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get badge", description = "Get badge details by ID")
    public ResponseEntity<BadgeDTO> getBadgeById(@PathVariable UUID id) {
        BadgeDTO badge = badgeService.getBadgeById(id);
        return ResponseEntity.ok(badge);
    }

    /**
     * Get student's badges
     */
    @GetMapping("/my-badges")
    @Operation(summary = "Get my badges", description = "Get badges earned by current student")
    public ResponseEntity<List<BadgeDTO>> getMyBadges(@AuthenticationPrincipal UserDetails userDetails) {
        // TODO: Extract actual student ID from authenticated user
        UUID studentId = UUID.randomUUID(); // Placeholder

        List<BadgeDTO> badges = badgeService.getStudentBadges(studentId);
        return ResponseEntity.ok(badges);
    }

    /**
     * Get student badges by student ID (for instructors)
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student badges", description = "Get badges earned by a specific student")
    public ResponseEntity<List<BadgeDTO>> getStudentBadges(@PathVariable UUID studentId) {
        List<BadgeDTO> badges = badgeService.getStudentBadges(studentId);
        return ResponseEntity.ok(badges);
    }
}