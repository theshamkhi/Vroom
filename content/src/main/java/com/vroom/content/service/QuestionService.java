package com.vroom.content.service;

import com.vroom.content.dto.CreateQuestionRequest;
import com.vroom.content.dto.QuestionDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for question management operations
 */
public interface QuestionService {

    QuestionDTO createQuestion(UUID scenarioId, CreateQuestionRequest request);

    List<QuestionDTO> getQuestionsByScenario(UUID scenarioId);

    QuestionDTO getQuestionById(UUID id);

    void deleteQuestion(UUID id);
}
