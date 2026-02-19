package com.vroom.content.service;

import com.vroom.content.dto.CreateScenarioRequest;
import com.vroom.content.dto.ScenarioDTO;
import com.vroom.content.model.enums.Difficulty;
import com.vroom.content.model.enums.Theme;

import java.util.List;
import java.util.UUID;

public interface ScenarioService {

    ScenarioDTO createScenario(CreateScenarioRequest request, UUID createdBy);

    ScenarioDTO getScenarioById(UUID id);

    ScenarioDTO getPublishedScenario(UUID id);

    List<ScenarioDTO> getAllPublishedScenarios();

    List<ScenarioDTO> getScenariosByDifficulty(Difficulty difficulty);

    List<ScenarioDTO> getScenariosByTheme(Theme theme);

    List<ScenarioDTO> searchScenarios(String keyword);

    List<ScenarioDTO> getScenariosByTag(String tag);

    ScenarioDTO updateScenario(UUID id, CreateScenarioRequest request, UUID updatedBy);

    ScenarioDTO publishScenario(UUID id, UUID publishedBy);

    ScenarioDTO unpublishScenario(UUID id, UUID unpublishedBy);

    void deleteScenario(UUID id);

    List<ScenarioDTO> getTopRatedScenarios(int limit);

    List<ScenarioDTO> getMostPopularScenarios(int limit);
}
