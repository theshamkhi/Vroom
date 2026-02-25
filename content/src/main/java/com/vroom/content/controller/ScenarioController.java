package com.vroom.content.controller;

import com.vroom.content.dto.CreateScenarioRequest;
import com.vroom.content.dto.ScenarioDTO;
import com.vroom.content.model.enums.Difficulty;
import com.vroom.content.model.enums.Theme;
import com.vroom.content.service.ScenarioService;
import com.vroom.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for scenario management
 */
@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Scenarios", description = "Interactive video scenario management")
public class ScenarioController {

    private final ScenarioService scenarioService;

    /**
     * Create a new scenario
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Create scenario", description = "Create a new interactive scenario (Instructor/Admin only)")
    public ResponseEntity<ScenarioDTO> createScenario(
            @Valid @RequestBody CreateScenarioRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID createdBy = SecurityUtils.getCurrentUserId();

        ScenarioDTO created = scenarioService.createScenario(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get all published scenarios
     */
    @GetMapping
    @Operation(summary = "Get all scenarios", description = "Get all published scenarios")
    public ResponseEntity<List<ScenarioDTO>> getAllScenarios() {
        List<ScenarioDTO> scenarios = scenarioService.getAllPublishedScenarios();
        return ResponseEntity.ok(scenarios);
    }

    /**
     * Get scenario by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get scenario by ID", description = "Get scenario details by ID")
    public ResponseEntity<ScenarioDTO> getScenarioById(@PathVariable UUID id) {
        ScenarioDTO scenario = scenarioService.getPublishedScenario(id);
        return ResponseEntity.ok(scenario);
    }

    /**
     * Get scenarios by difficulty
     */
    @GetMapping("/difficulty/{difficulty}")
    @Operation(summary = "Get scenarios by difficulty", description = "Filter scenarios by difficulty level")
    public ResponseEntity<List<ScenarioDTO>> getScenariosByDifficulty(@PathVariable Difficulty difficulty) {
        List<ScenarioDTO> scenarios = scenarioService.getScenariosByDifficulty(difficulty);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * Get scenarios by theme
     */
    @GetMapping("/theme/{theme}")
    @Operation(summary = "Get scenarios by theme", description = "Filter scenarios by theme")
    public ResponseEntity<List<ScenarioDTO>> getScenariosByTheme(@PathVariable Theme theme) {
        List<ScenarioDTO> scenarios = scenarioService.getScenariosByTheme(theme);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * Search scenarios
     */
    @GetMapping("/search")
    @Operation(summary = "Search scenarios", description = "Search scenarios by keyword")
    public ResponseEntity<List<ScenarioDTO>> searchScenarios(@RequestParam String keyword) {
        List<ScenarioDTO> scenarios = scenarioService.searchScenarios(keyword);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * Get scenarios by tag
     */
    @GetMapping("/tag/{tag}")
    @Operation(summary = "Get scenarios by tag", description = "Filter scenarios by tag")
    public ResponseEntity<List<ScenarioDTO>> getScenariosByTag(@PathVariable String tag) {
        List<ScenarioDTO> scenarios = scenarioService.getScenariosByTag(tag);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * Get top rated scenarios
     */
    @GetMapping("/top-rated")
    @Operation(summary = "Get top rated scenarios", description = "Get highest rated scenarios")
    public ResponseEntity<List<ScenarioDTO>> getTopRatedScenarios(@RequestParam(defaultValue = "10") int limit) {
        List<ScenarioDTO> scenarios = scenarioService.getTopRatedScenarios(limit);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * Get most popular scenarios
     */
    @GetMapping("/popular")
    @Operation(summary = "Get popular scenarios", description = "Get most completed scenarios")
    public ResponseEntity<List<ScenarioDTO>> getMostPopularScenarios(@RequestParam(defaultValue = "10") int limit) {
        List<ScenarioDTO> scenarios = scenarioService.getMostPopularScenarios(limit);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * Update scenario
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Update scenario", description = "Update scenario details (Instructor/Admin only)")
    public ResponseEntity<ScenarioDTO> updateScenario(
            @PathVariable UUID id,
            @Valid @RequestBody CreateScenarioRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID updatedBy = SecurityUtils.getCurrentUserId();

        ScenarioDTO updated = scenarioService.updateScenario(id, request, updatedBy);
        return ResponseEntity.ok(updated);
    }

    /**
     * Publish scenario
     */
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Publish scenario", description = "Make scenario available to students (Instructor/Admin only)")
    public ResponseEntity<ScenarioDTO> publishScenario(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID publishedBy = SecurityUtils.getCurrentUserId();

        ScenarioDTO published = scenarioService.publishScenario(id, publishedBy);
        return ResponseEntity.ok(published);
    }

    /**
     * Unpublish scenario
     */
    @PostMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unpublish scenario", description = "Hide scenario from students (Admin only)")
    public ResponseEntity<ScenarioDTO> unpublishScenario(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID unpublishedBy = SecurityUtils.getCurrentUserId();

        ScenarioDTO unpublished = scenarioService.unpublishScenario(id, unpublishedBy);
        return ResponseEntity.ok(unpublished);
    }

    /**
     * Delete scenario
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete scenario", description = "Delete scenario permanently (Admin only)")
    public ResponseEntity<Void> deleteScenario(@PathVariable UUID id) {
        scenarioService.deleteScenario(id);
        return ResponseEntity.noContent().build();
    }
}