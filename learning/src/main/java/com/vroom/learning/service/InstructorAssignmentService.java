package com.vroom.learning.service;

import com.vroom.content.model.entity.Scenario;
import com.vroom.content.repository.ScenarioRepository;
import com.vroom.learning.dto.AssignmentDTO;
import com.vroom.learning.dto.CreateAssignmentRequest;
import com.vroom.learning.model.entity.Assignment;
import com.vroom.learning.model.enums.AssignmentStatus;
import com.vroom.learning.repository.AssignmentRepository;
import com.vroom.security.model.entity.Student;
import com.vroom.security.repository.StudentRepository;
import com.vroom.security.util.SecurityUtils;
import com.vroom.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorAssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final ScenarioRepository scenarioRepository;

    @Transactional
    public AssignmentDTO assignScenario(CreateAssignmentRequest request) {
        UUID instructorId = SecurityUtils.getCurrentUserId();

        studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        Assignment assignment = Assignment.builder()
                .studentId(request.getStudentId())
                .scenarioId(request.getScenarioId())
                .instructorId(instructorId)
                .dueDate(request.getDueDate())
                .note(request.getNote())
                .status(AssignmentStatus.PENDING)
                .build();

        assignment = assignmentRepository.save(assignment);
        return enrichAndToDTO(assignment);
    }

    public List<AssignmentDTO> getInstructorAssignments(UUID instructorId, boolean isAdmin) {
        List<Assignment> assignments = isAdmin
                ? assignmentRepository.findAll()
                : assignmentRepository.findByInstructorIdOrderByCreatedAtDesc(instructorId);
        return assignments.stream().map(this::enrichAndToDTO).toList();
    }

    public List<AssignmentDTO> getStudentAssignments(UUID instructorId, boolean isAdmin, UUID studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        List<Assignment> assignments = assignmentRepository.findByStudentIdOrderByDueDateAsc(studentId);
        return assignments.stream()
                .filter(a -> isAdmin || instructorId.equals(a.getInstructorId()))
                .map(this::enrichAndToDTO)
                .toList();
    }

    public List<AssignmentDTO> getMyAssignments(UUID studentId) {
        return assignmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::enrichAndToDTO)
                .toList();
    }

    @Transactional
    public void revokeAssignment(UUID instructorId, boolean isAdmin, UUID assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));

        if (!isAdmin && !instructorId.equals(assignment.getInstructorId())) {
            throw new RuntimeException("Forbidden");
        }

        assignmentRepository.deleteById(assignmentId);
    }

    private AssignmentDTO enrichAndToDTO(Assignment assignment) {
        Student student = studentRepository.findById(assignment.getStudentId()).orElse(null);
        Scenario scenario = assignment.getScenarioId() != null
                ? scenarioRepository.findById(assignment.getScenarioId()).orElse(null)
                : null;
        return toDTO(assignment, student, scenario);
    }

    private AssignmentDTO toDTO(Assignment assignment, Student student, Scenario scenario) {
        return AssignmentDTO.builder()
                .id(assignment.getId())
                .studentId(assignment.getStudentId())
                .studentName(student != null ? (student.getFirstName() + " " + student.getLastName()) : null)
                .studentEmail(student != null ? student.getEmail() : null)
                .scenarioId(scenario != null ? scenario.getId() : assignment.getScenarioId())
                .scenarioTitle(scenario != null ? scenario.getTitle() : null)
                .assignedAt(assignment.getCreatedAt())
                .dueDate(assignment.getDueDate())
                .note(assignment.getNote())
                .completedAt(assignment.getCompletedAt())
                .status(mapStatus(assignment))
                .build();
    }

    private String mapStatus(Assignment assignment) {
        if (assignment.getCompletedAt() != null
                || assignment.getStatus() == AssignmentStatus.COMPLETED
                || assignment.getStatus() == AssignmentStatus.GRADED) {
            return "COMPLETED";
        }
        if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
            return "OVERDUE";
        }
        return "PENDING";
    }
}