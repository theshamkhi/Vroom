package com.vroom.security.controller;

import com.vroom.security.dto.InstructorStudentDTO;
import com.vroom.security.dto.UpdateStudentNotesRequest;
import com.vroom.security.model.entity.Student;
import com.vroom.security.repository.StudentRepository;
import com.vroom.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/instructor/students")
@RequiredArgsConstructor
@Slf4j
public class InstructorStudentController {

    private final StudentRepository studentRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<InstructorStudentDTO>> getMyStudents() {
        List<Student> students = studentRepository.findAll();
        List<InstructorStudentDTO> result = students.stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{studentId}/notes")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<InstructorStudentDTO> updateStudentNotes(
            @PathVariable UUID studentId,
            @Valid @RequestBody UpdateStudentNotesRequest request) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        student.setInstructorNotes(request.getNotes());
        Student saved = studentRepository.save(student);

        return ResponseEntity.ok(mapToDTO(saved));
    }

    private InstructorStudentDTO mapToDTO(Student s) {
        return InstructorStudentDTO.builder()
                .id(s.getId())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .email(s.getEmail())
                .currentLevel(s.getCurrentLevel())
                .totalPoints(s.getTotalPoints())
                .completionPercentage(s.getCompletionPercentage())
                .scenariosCompleted(s.getScenariosCompleted())
                .badgesEarned(s.getBadgesEarned())
                .enrollmentDate(s.getEnrollmentDate())
                .targetCompletionDate(s.getTargetCompletionDate())
                .assignedInstructorId(s.getAssignedInstructorId())
                .drivingSchool(s.getDrivingSchool())
                .instructorNotes(s.getInstructorNotes())
                .build();
    }
}
