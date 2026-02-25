package com.vroom.learning.controller;

import com.vroom.learning.dto.ProgressDTO;
import com.vroom.learning.dto.SubmitAnswerRequest;
import com.vroom.learning.service.ProgressService;
import com.vroom.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for student progress tracking
 */
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Progress", description = "Student progress tracking")
public class ProgressController {

    private final ProgressService progressService;

    /**
     * Start a scenario
     */
    @PostMapping("/scenarios/{scenarioId}/start")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Start scenario", description = "Begin a scenario")
    public ResponseEntity<ProgressDTO> startScenario(@PathVariable UUID scenarioId) {

        UUID studentId = SecurityUtils.getCurrentUserId();

        ProgressDTO progress = progressService.startScenario(studentId, scenarioId);
        return ResponseEntity.ok(progress);
    }

    /**
     * Submit answer to question
     */
    @PostMapping("/answers")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit answer", description = "Submit answer to a question")
    public ResponseEntity<Void> submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {

        UUID studentId = SecurityUtils.getCurrentUserId();

        progressService.submitAnswer(studentId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Complete scenario
     */
    @PostMapping("/scenarios/{scenarioId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Complete scenario", description = "Mark scenario as complete")
    public ResponseEntity<ProgressDTO> completeScenario(
            @PathVariable UUID scenarioId,
            @RequestParam Double score,
            @RequestParam Integer pointsEarned) {

        UUID studentId = SecurityUtils.getCurrentUserId();

        ProgressDTO progress = progressService.completeScenario(studentId, scenarioId, score, pointsEarned);
        return ResponseEntity.ok(progress);
    }

    /**
     * Get my progress
     */
    @GetMapping("/my-progress")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get my progress", description = "Get all my scenario progress")
    public ResponseEntity<List<ProgressDTO>> getMyProgress() {

        UUID studentId = SecurityUtils.getCurrentUserId();

        List<ProgressDTO> progress = progressService.getStudentProgress(studentId);
        return ResponseEntity.ok(progress);
    }

    /**
     * Get progress for specific scenario
     */
    @GetMapping("/scenarios/{scenarioId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get scenario progress", description = "Get progress on specific scenario")
    public ResponseEntity<ProgressDTO> getScenarioProgress(@PathVariable UUID scenarioId) {

        UUID studentId = SecurityUtils.getCurrentUserId();

        ProgressDTO progress = progressService.getScenarioProgress(studentId, scenarioId);
        return progress != null ? ResponseEntity.ok(progress) : ResponseEntity.notFound().build();
    }
}