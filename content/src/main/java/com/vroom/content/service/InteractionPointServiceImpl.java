package com.vroom.content.service;

import com.vroom.content.dto.CreateInteractionPointRequest;
import com.vroom.content.dto.InteractionPointDTO;
import com.vroom.content.model.entity.InteractionPoint;
import com.vroom.content.repository.InteractionPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for interaction point management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InteractionPointServiceImpl implements InteractionPointService {

    private final InteractionPointRepository interactionPointRepository;

    /**
     * Create a new interaction point for a scenario
     */
    @Transactional
    public InteractionPointDTO createInteractionPoint(UUID scenarioId, CreateInteractionPointRequest request) {
        log.info("Creating new interaction point for scenario: {} at timestamp: {}",
                scenarioId, request.getTimestampSeconds());

        // Check if interaction point already exists at this timestamp
        if (interactionPointRepository.existsByScenarioIdAndTimestampSeconds(scenarioId, request.getTimestampSeconds())) {
            throw new RuntimeException("Interaction point already exists at timestamp: " + request.getTimestampSeconds());
        }

        InteractionPoint interactionPoint = InteractionPoint.builder()
                .scenarioId(scenarioId)
                .questionId(request.getQuestionId())
                .timestampSeconds(request.getTimestampSeconds())
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0)
                .mandatory(request.getMandatory() != null ? request.getMandatory() : true)
                .build();

        InteractionPoint saved = interactionPointRepository.save(interactionPoint);
        log.info("Interaction point created successfully: {}", saved.getId());

        return mapToDTO(saved);
    }

    /**
     * Get all interaction points for a scenario (ordered by timestamp)
     */
    public List<InteractionPointDTO> getInteractionPointsByScenario(UUID scenarioId) {
        log.debug("Fetching interaction points for scenario: {}", scenarioId);

        return interactionPointRepository.findByScenarioIdOrderByTimestampSecondsAsc(scenarioId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get interaction point by ID
     */
    public InteractionPointDTO getInteractionPointById(UUID id) {
        log.debug("Fetching interaction point: {}", id);

        InteractionPoint interactionPoint = interactionPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interaction point not found with id: " + id));

        return mapToDTO(interactionPoint);
    }

    /**
     * Delete interaction point
     */
    @Transactional
    public void deleteInteractionPoint(UUID id) {
        log.info("Deleting interaction point: {}", id);

        interactionPointRepository.deleteById(id);
        log.info("Interaction point deleted successfully: {}", id);
    }

    /**
     * Map entity to DTO
     */
    private InteractionPointDTO mapToDTO(InteractionPoint interactionPoint) {
        return InteractionPointDTO.builder()
                .id(interactionPoint.getId())
                .scenarioId(interactionPoint.getScenarioId())
                .questionId(interactionPoint.getQuestionId())
                .timestampSeconds(interactionPoint.getTimestampSeconds())
                .timestampFormatted(interactionPoint.getTimestampFormatted())
                .title(interactionPoint.getTitle())
                .description(interactionPoint.getDescription())
                .orderIndex(interactionPoint.getOrderIndex())
                .mandatory(interactionPoint.getMandatory())
                .build();
    }
}