package com.vroom.content.service;

import com.vroom.content.dto.CreateScenarioRequest;
import com.vroom.content.dto.ScenarioDTO;
import com.vroom.content.model.entity.Scenario;
import com.vroom.content.model.enums.Difficulty;
import com.vroom.content.model.enums.Theme;
import com.vroom.content.repository.InteractionPointRepository;
import com.vroom.content.repository.QuestionRepository;
import com.vroom.content.repository.ScenarioRepository;
import com.vroom.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for scenario management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioServiceImpl implements ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final QuestionRepository questionRepository;
    private final InteractionPointRepository interactionPointRepository;

    /**
     * Create a new scenario
     */
    @Transactional
    public ScenarioDTO createScenario(CreateScenarioRequest request, UUID createdBy) {
        log.info("Creating new scenario: {}", request.getTitle());

        Scenario scenario = Scenario.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .difficulty(request.getDifficulty())
                .theme(request.getTheme())
                .videoId(request.getVideoId())
                .durationSeconds(request.getDurationSeconds() != null ? request.getDurationSeconds() : 0)
                .estimatedMinutes(request.getEstimatedMinutes() != null ? request.getEstimatedMinutes() : 5)
                .tags(request.getTags())
                .prerequisiteIds(request.getPrerequisiteIds())
                .thumbnailUrl(request.getThumbnailUrl())
                .learningObjectives(request.getLearningObjectives())
                .maxPoints(request.getMaxPoints() != null ? request.getMaxPoints() : 100)
                .passingScore(request.getPassingScore() != null ? request.getPassingScore() : 70)
                .createdBy(createdBy)
                .published(false)
                .build();

        Scenario savedScenario = scenarioRepository.save(scenario);
        log.info("Scenario created successfully: {}", savedScenario.getId());

        return mapToDTO(savedScenario);
    }

    /**
     * Get scenario by ID
     */
    @Cacheable(value = "scenarios", key = "#id")
    public ScenarioDTO getScenarioById(UUID id) {
        log.debug("Fetching scenario: {}", id);

        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + id));

        return mapToDTO(scenario);
    }

    /**
     * Get published scenario by ID (for students)
     */
    @Cacheable(value = "publishedScenarios", key = "#id")
    public ScenarioDTO getPublishedScenario(UUID id) {
        log.debug("Fetching published scenario: {}", id);

        Scenario scenario = scenarioRepository.findByIdAndPublishedTrue(id)
                .orElseThrow(() -> new RuntimeException("Published scenario not found with id: " + id));

        return mapToDTO(scenario);
    }

    /**
     * Get all published scenarios
     */
    @Cacheable(value = "publishedScenariosList")
    public List<ScenarioDTO> getAllPublishedScenarios() {
        log.debug("Fetching all published scenarios");

        return scenarioRepository.findByPublishedTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get scenarios by difficulty
     */
    public List<ScenarioDTO> getScenariosByDifficulty(Difficulty difficulty) {
        log.debug("Fetching scenarios by difficulty: {}", difficulty);

        return scenarioRepository.findByDifficultyAndPublishedTrue(difficulty)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get scenarios by theme
     */
    public List<ScenarioDTO> getScenariosByTheme(Theme theme) {
        log.debug("Fetching scenarios by theme: {}", theme);

        return scenarioRepository.findByThemeAndPublishedTrue(theme)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Search scenarios by keyword
     */
    public List<ScenarioDTO> searchScenarios(String keyword) {
        log.debug("Searching scenarios with keyword: {}", keyword);

        return scenarioRepository.searchByKeyword(keyword)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get scenarios by tag
     */
    public List<ScenarioDTO> getScenariosByTag(String tag) {
        log.debug("Fetching scenarios by tag: {}", tag);

        return scenarioRepository.findByTag(tag)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Update scenario
     */
    @Transactional
    @CacheEvict(value = {"scenarios", "publishedScenarios", "publishedScenariosList"}, allEntries = true)
    public ScenarioDTO updateScenario(UUID id, CreateScenarioRequest request, UUID updatedBy) {
        log.info("Updating scenario: {}", id);

        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + id));

        scenario.setTitle(request.getTitle());
        scenario.setDescription(request.getDescription());
        scenario.setDifficulty(request.getDifficulty());
        scenario.setTheme(request.getTheme());
        scenario.setVideoId(request.getVideoId());
        scenario.setDurationSeconds(request.getDurationSeconds());
        scenario.setEstimatedMinutes(request.getEstimatedMinutes());
        scenario.setTags(request.getTags());
        scenario.setPrerequisiteIds(request.getPrerequisiteIds());
        scenario.setThumbnailUrl(request.getThumbnailUrl());
        scenario.setLearningObjectives(request.getLearningObjectives());
        scenario.setMaxPoints(request.getMaxPoints());
        scenario.setPassingScore(request.getPassingScore());
        scenario.setLastModifiedBy(updatedBy);

        Scenario updatedScenario = scenarioRepository.save(scenario);
        log.info("Scenario updated successfully: {}", id);

        return mapToDTO(updatedScenario);
    }

    /**
     * Publish scenario
     */
    @Transactional
    @CacheEvict(value = {"scenarios", "publishedScenarios", "publishedScenariosList"}, allEntries = true)
    public ScenarioDTO publishScenario(UUID id, UUID publishedBy) {
        log.info("Publishing scenario: {}", id);

        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + id));

        scenario.publish(publishedBy);
        Scenario published = scenarioRepository.save(scenario);

        log.info("Scenario published successfully: {}", id);
        return mapToDTO(published);
    }

    /**
     * Unpublish scenario
     */
    @Transactional
    @CacheEvict(value = {"scenarios", "publishedScenarios", "publishedScenariosList"}, allEntries = true)
    public ScenarioDTO unpublishScenario(UUID id, UUID unpublishedBy) {
        log.info("Unpublishing scenario: {}", id);

        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + id));

        scenario.unpublish(unpublishedBy);
        Scenario unpublished = scenarioRepository.save(scenario);

        log.info("Scenario unpublished successfully: {}", id);
        return mapToDTO(unpublished);
    }

    /**
     * Delete scenario
     */
    @Transactional
    @CacheEvict(value = {"scenarios", "publishedScenarios", "publishedScenariosList"}, allEntries = true)
    public void deleteScenario(UUID id) {
        log.info("Deleting scenario: {}", id);

        if (!scenarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Scenario", "id", id);
        }

        // Delete related interaction points and questions
        interactionPointRepository.deleteByScenarioId(id);
        questionRepository.deleteByScenarioId(id);

        scenarioRepository.deleteById(id);
        log.info("Scenario deleted successfully: {}", id);
    }

    /**
     * Get top rated scenarios
     */
    public List<ScenarioDTO> getTopRatedScenarios(int limit) {
        log.debug("Fetching top {} rated scenarios", limit);

        Pageable pageable = PageRequest.of(0, limit);
        return scenarioRepository.findTopRatedScenarios(pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get most popular scenarios
     */
    public List<ScenarioDTO> getMostPopularScenarios(int limit) {
        log.debug("Fetching top {} popular scenarios", limit);

        Pageable pageable = PageRequest.of(0, limit);
        return scenarioRepository.findMostPopularScenarios(pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Map entity to DTO
     */
    private ScenarioDTO mapToDTO(Scenario scenario) {
        return ScenarioDTO.builder()
                .id(scenario.getId())
                .title(scenario.getTitle())
                .description(scenario.getDescription())
                .difficulty(scenario.getDifficulty())
                .theme(scenario.getTheme())
                .videoId(scenario.getVideoId())
                .durationSeconds(scenario.getDurationSeconds())
                .estimatedMinutes(scenario.getEstimatedMinutes())
                .tags(scenario.getTags())
                .prerequisiteIds(scenario.getPrerequisiteIds())
                .thumbnailUrl(scenario.getThumbnailUrl())
                .learningObjectives(scenario.getLearningObjectives())
                .maxPoints(scenario.getMaxPoints())
                .passingScore(scenario.getPassingScore())
                .published(scenario.getPublished())
                .completionCount(scenario.getCompletionCount())
                .averageScore(scenario.getAverageScore())
                .averageCompletionTime(scenario.getAverageCompletionTime())
                .createdBy(scenario.getCreatedBy())
                .createdAt(scenario.getCreatedAt())
                .publishedAt(scenario.getPublishedAt())
                .questionCount((int) questionRepository.countByScenarioId(scenario.getId()))
                .interactionPointCount((int) interactionPointRepository.countByScenarioId(scenario.getId()))
                .build();
    }
}