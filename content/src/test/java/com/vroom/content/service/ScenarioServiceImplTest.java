package com.vroom.content.service;

import com.vroom.content.dto.CreateScenarioRequest;
import com.vroom.content.model.entity.Scenario;
import com.vroom.content.model.enums.Difficulty;
import com.vroom.content.model.enums.Theme;
import com.vroom.content.repository.InteractionPointRepository;
import com.vroom.content.repository.QuestionRepository;
import com.vroom.content.repository.ScenarioRepository;
import com.vroom.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScenarioServiceImplTest {

    @Mock
    private ScenarioRepository scenarioRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private InteractionPointRepository interactionPointRepository;

    @InjectMocks
    private ScenarioServiceImpl service;

    private UUID scenarioId;

    @BeforeEach
    void setUp() {
        scenarioId = UUID.randomUUID();
    }

    private void setupCounts() {
        lenient().when(questionRepository.countByScenarioId(scenarioId)).thenReturn(0L);
        lenient().when(interactionPointRepository.countByScenarioId(scenarioId)).thenReturn(0L);
    }

    @Test
    void updateScenario_whenEstimatedMinutesMissingButDurationProvided_calculatesEstimatedMinutes() {
        setupCounts();
        UUID updatedBy = UUID.randomUUID();

        Scenario scenario = Scenario.builder()
                .id(scenarioId)
                .title("Old title")
                .description("Old description")
                .difficulty(Difficulty.BEGINNER)
                .theme(Theme.URBAN_DRIVING)
                .estimatedMinutes(5)
                .durationSeconds(10)
                .createdBy(UUID.randomUUID())
                .published(false)
                .build();

        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
        when(scenarioRepository.save(any(Scenario.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateScenarioRequest request = CreateScenarioRequest.builder()
                .title("New title")
                .description("New description")
                .difficulty(Difficulty.INTERMEDIATE)
                .theme(Theme.HIGHWAY)
                .durationSeconds(121)
                .estimatedMinutes(null)
                .build();

        var dto = service.updateScenario(scenarioId, request, updatedBy);

        assertEquals(3, dto.getEstimatedMinutes());
        assertEquals(121, dto.getDurationSeconds());
        assertEquals("New title", dto.getTitle());
    }

    @Test
    void updateScenario_whenNoEstimatedMinutesAndNoDurationAndScenarioEstimatedMinutesNull_throwsIllegalArgumentException() {
        Scenario scenario = Scenario.builder()
                .id(scenarioId)
                .title("Title")
                .description("Description")
                .difficulty(Difficulty.BEGINNER)
                .theme(Theme.URBAN_DRIVING)
                .estimatedMinutes(null)
                .durationSeconds(10)
                .createdBy(UUID.randomUUID())
                .published(false)
                .build();

        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));

        CreateScenarioRequest request = CreateScenarioRequest.builder()
                .title("New title")
                .description("New description")
                .difficulty(Difficulty.BEGINNER)
                .theme(Theme.URBAN_DRIVING)
                .durationSeconds(null)
                .estimatedMinutes(null)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateScenario(scenarioId, request, UUID.randomUUID()));

        assertTrue(ex.getMessage().contains("estimatedMinutes"));
        verify(scenarioRepository, never()).save(any());
    }

    @Test
    void deleteScenario_whenScenarioDoesNotExist_throwsResourceNotFoundException() {
        when(scenarioRepository.existsById(scenarioId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteScenario(scenarioId));

        verify(interactionPointRepository, never()).deleteByScenarioId(any());
        verify(questionRepository, never()).deleteByScenarioId(any());
        verify(scenarioRepository, never()).deleteById(any());
    }
}
