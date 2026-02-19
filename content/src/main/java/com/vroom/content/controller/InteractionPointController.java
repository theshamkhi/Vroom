package com.vroom.content.controller;

import com.vroom.content.dto.CreateInteractionPointRequest;
import com.vroom.content.dto.CreateQuestionRequest;
import com.vroom.content.dto.InteractionPointDTO;
import com.vroom.content.dto.QuestionDTO;
import com.vroom.content.service.InteractionPointService;
import com.vroom.content.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for interaction points and questions
 */
@RestController
@RequestMapping("/api/scenarios/{scenarioId}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Interaction Points", description = "Manage scenario interaction points and questions")
public class InteractionPointController {

    private final InteractionPointService interactionPointService;
    private final QuestionService questionService;

    /**
     * Create question for scenario
     */
    @PostMapping("/questions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Create question", description = "Create a new question for scenario")
    public ResponseEntity<QuestionDTO> createQuestion(
            @PathVariable UUID scenarioId,
            @Valid @RequestBody CreateQuestionRequest request) {

        QuestionDTO created = questionService.createQuestion(scenarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get all questions for scenario
     */
    @GetMapping("/questions")
    @Operation(summary = "Get questions", description = "Get all questions for scenario")
    public ResponseEntity<List<QuestionDTO>> getQuestions(@PathVariable UUID scenarioId) {
        List<QuestionDTO> questions = questionService.getQuestionsByScenario(scenarioId);
        return ResponseEntity.ok(questions);
    }

    /**
     * Create interaction point for scenario
     */
    @PostMapping("/interaction-points")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Create interaction point", description = "Create timestamp-based interaction point")
    public ResponseEntity<InteractionPointDTO> createInteractionPoint(
            @PathVariable UUID scenarioId,
            @Valid @RequestBody CreateInteractionPointRequest request) {

        InteractionPointDTO created = interactionPointService.createInteractionPoint(scenarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get all interaction points for scenario
     */
    @GetMapping("/interaction-points")
    @Operation(summary = "Get interaction points", description = "Get all interaction points for scenario")
    public ResponseEntity<List<InteractionPointDTO>> getInteractionPoints(@PathVariable UUID scenarioId) {
        List<InteractionPointDTO> interactionPoints = interactionPointService.getInteractionPointsByScenario(scenarioId);
        return ResponseEntity.ok(interactionPoints);
    }

    /**
     * Delete interaction point
     */
    @DeleteMapping("/interaction-points/{interactionPointId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Delete interaction point", description = "Delete interaction point")
    public ResponseEntity<Void> deleteInteractionPoint(
            @PathVariable UUID scenarioId,
            @PathVariable UUID interactionPointId) {

        interactionPointService.deleteInteractionPoint(interactionPointId);
        return ResponseEntity.noContent().build();
    }
}