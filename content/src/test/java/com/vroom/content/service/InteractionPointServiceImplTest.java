package com.vroom.content.service;

import com.vroom.content.dto.CreateInteractionPointRequest;
import com.vroom.content.model.entity.InteractionPoint;
import com.vroom.content.repository.InteractionPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InteractionPointServiceImplTest {

    @Mock
    private InteractionPointRepository repo;

    @InjectMocks
    private InteractionPointServiceImpl service;

    private UUID scenarioId;

    @BeforeEach
    void setUp() {
        scenarioId = UUID.randomUUID();
    }

    @Test
    void createInteractionPoint_whenTimestampAlreadyExists_throwsRuntimeException() {
        CreateInteractionPointRequest request = CreateInteractionPointRequest.builder()
                .questionId(UUID.randomUUID())
                .timestampSeconds(45)
                .build();

        when(repo.existsByScenarioIdAndTimestampSeconds(scenarioId, 45)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createInteractionPoint(scenarioId, request));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(repo, never()).save(any());
    }

    @Test
    void createInteractionPoint_whenOptionalFieldsNull_setsDefaults() {
        UUID questionId = UUID.randomUUID();

        CreateInteractionPointRequest request = CreateInteractionPointRequest.builder()
                .questionId(questionId)
                .timestampSeconds(90)
                .orderIndex(null)
                .mandatory(null)
                .build();

        when(repo.existsByScenarioIdAndTimestampSeconds(scenarioId, 90)).thenReturn(false);
        when(repo.save(any(InteractionPoint.class))).thenAnswer(inv -> inv.getArgument(0));

        var dto = service.createInteractionPoint(scenarioId, request);

        assertEquals(scenarioId, dto.getScenarioId());
        assertEquals(questionId, dto.getQuestionId());
        assertEquals(90, dto.getTimestampSeconds());
        assertEquals(0, dto.getOrderIndex());
        assertTrue(dto.getMandatory());
        assertEquals("01:30", dto.getTimestampFormatted());

        verify(repo).save(any(InteractionPoint.class));
    }
}
