package com.vroom.security.controller;

import com.vroom.security.dto.*;
import com.vroom.security.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminUserDTO>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getUsers(pageable));
    }

    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserDTO> updateUserRole(
            @PathVariable("id") UUID userId,
            @Valid @RequestBody UpdateUserRoleRequest request) {

        return ResponseEntity.ok(adminService.updateUserRole(userId, request.getRole()));
    }

    @PatchMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserDTO> updateUserStatus(
            @PathVariable("id") UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        return ResponseEntity.ok(adminService.updateUserStatus(userId, request.getEnabled()));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/instructors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminInstructorDTO>> getInstructors(Pageable pageable) {
        return ResponseEntity.ok(adminService.getInstructors(pageable));
    }

    @PatchMapping("/instructors/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminInstructorDTO> setInstructorApproved(
            @PathVariable("id") UUID instructorId,
            @Valid @RequestBody ApproveInstructorRequest request) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(adminService.setInstructorApproved(instructorId, request.getEnabled()));
    }
}
