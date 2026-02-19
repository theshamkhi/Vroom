package com.vroom.content.service;

import com.vroom.content.dto.CreateInteractionPointRequest;
import com.vroom.content.dto.InteractionPointDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for interaction point management operations
 */
public interface InteractionPointService {

    InteractionPointDTO createInteractionPoint(UUID scenarioId, CreateInteractionPointRequest request);

    List<InteractionPointDTO> getInteractionPointsByScenario(UUID scenarioId);

    InteractionPointDTO getInteractionPointById(UUID id);

    void deleteInteractionPoint(UUID id);
}
