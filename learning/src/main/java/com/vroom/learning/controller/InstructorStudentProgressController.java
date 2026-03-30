package com.vroom.learning.controller;

import com.vroom.learning.dto.ProgressDTO;
import com.vroom.learning.service.ProgressService;
import com.vroom.security.model.entity.Student;
import com.vroom.security.repository.StudentRepository;
import com.vroom.security.util.SecurityUtils;
import com.vroom.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/instructor/students")
@RequiredArgsConstructor
public class InstructorStudentProgressController {

    private final ProgressService progressService;
    private final StudentRepository studentRepository;

    @GetMapping("/{studentId}/progress")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<ProgressDTO>> getStudentProgress(@PathVariable UUID studentId) {
        UUID requesterId = SecurityUtils.getCurrentUserId();

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        if (!SecurityUtils.hasRole("ADMIN")) {
            if (student.getAssignedInstructorId() == null || !student.getAssignedInstructorId().equals(requesterId)) {
                return ResponseEntity.status(403).<List<ProgressDTO>>build();
            }
        }

        List<ProgressDTO> progress = progressService.getStudentProgress(studentId);
        return ResponseEntity.ok(progress);
    }
}
