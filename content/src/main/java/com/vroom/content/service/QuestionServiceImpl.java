package com.vroom.content.service;

import com.vroom.content.dto.*;
import com.vroom.content.model.entity.Answer;
import com.vroom.content.model.entity.Question;
import com.vroom.content.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for question management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * Create a new question for a scenario
     */
    @Transactional
    public QuestionDTO createQuestion(UUID scenarioId, CreateQuestionRequest request) {
        log.info("Creating new question for scenario: {}", scenarioId);

        Question question = Question.builder()
                .scenarioId(scenarioId)
                .type(request.getType())
                .questionText(request.getQuestionText())
                .hint(request.getHint())
                .explanation(request.getExplanation())
                .points(request.getPoints() != null ? request.getPoints() : 10)
                .timeLimitSeconds(request.getTimeLimitSeconds())
                .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0)
                .build();

        // Add answers
        if (request.getAnswers() != null) {
            for (int i = 0; i < request.getAnswers().size(); i++) {
                CreateAnswerRequest answerReq = request.getAnswers().get(i);
                Answer answer = Answer.builder()
                        .answerText(answerReq.getAnswerText())
                        .isCorrect(answerReq.getIsCorrect())
                        .orderIndex(answerReq.getOrderIndex() != null ? answerReq.getOrderIndex() : i)
                        .explanation(answerReq.getExplanation())
                        .imageUrl(answerReq.getImageUrl())
                        .build();
                question.addAnswer(answer);
            }
        }

        Question savedQuestion = questionRepository.save(question);
        log.info("Question created successfully: {}", savedQuestion.getId());

        return mapToDTO(savedQuestion);
    }

    /**
     * Get all questions for a scenario
     */
    public List<QuestionDTO> getQuestionsByScenario(UUID scenarioId) {
        log.debug("Fetching questions for scenario: {}", scenarioId);

        return questionRepository.findByScenarioIdOrderByOrderIndexAsc(scenarioId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get question by ID
     */
    public QuestionDTO getQuestionById(UUID id) {
        log.debug("Fetching question: {}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        return mapToDTO(question);
    }

    /**
     * Delete question
     */
    @Transactional
    public void deleteQuestion(UUID id) {
        log.info("Deleting question: {}", id);

        questionRepository.deleteById(id);
        log.info("Question deleted successfully: {}", id);
    }

    /**
     * Map entity to DTO
     */
    private QuestionDTO mapToDTO(Question question) {
        List<AnswerDTO> answerDTOs = question.getAnswers().stream()
                .map(this::mapAnswerToDTO)
                .toList();

        return QuestionDTO.builder()
                .id(question.getId())
                .scenarioId(question.getScenarioId())
                .type(question.getType())
                .questionText(question.getQuestionText())
                .hint(question.getHint())
                .explanation(question.getExplanation())
                .points(question.getPoints())
                .timeLimitSeconds(question.getTimeLimitSeconds())
                .orderIndex(question.getOrderIndex())
                .answers(answerDTOs)
                .attemptCount(question.getAttemptCount())
                .correctCount(question.getCorrectCount())
                .successRate(question.getSuccessRate())
                .createdAt(question.getCreatedAt())
                .build();
    }

    private AnswerDTO mapAnswerToDTO(Answer answer) {
        return AnswerDTO.builder()
                .id(answer.getId())
                .answerText(answer.getAnswerText())
                .isCorrect(answer.getIsCorrect())
                .orderIndex(answer.getOrderIndex())
                .explanation(answer.getExplanation())
                .imageUrl(answer.getImageUrl())
                .build();
    }
}