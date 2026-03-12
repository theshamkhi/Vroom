package com.vroom.learning.controller;

import com.vroom.learning.dto.AssignmentDTO;
import com.vroom.learning.dto.CreateAssignmentRequest;
import com.vroom.learning.service.InstructorAssignmentService;
import com.vroom.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorAssignmentController {

    private final InstructorAssignmentService instructorAssignmentService;

    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AssignmentDTO> assignScenario(@Valid @RequestBody CreateAssignmentRequest request) {
        AssignmentDTO created = instructorAssignmentService.assignScenario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/assignments")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsForInstructorStudents() {
        UUID instructorId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        List<AssignmentDTO> assignments = instructorAssignmentService.getInstructorAssignments(instructorId, isAdmin);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/students/{studentId}/assignments")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsForStudent(@PathVariable UUID studentId) {
        UUID instructorId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        List<AssignmentDTO> assignments = instructorAssignmentService.getStudentAssignments(instructorId, isAdmin, studentId);
        return ResponseEntity.ok(assignments);
    }

    @DeleteMapping("/assignments/{assignmentId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> revokeAssignment(@PathVariable UUID assignmentId) {
        UUID instructorId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        instructorAssignmentService.revokeAssignment(instructorId, isAdmin, assignmentId);
        return ResponseEntity.noContent().build();
    }
}
