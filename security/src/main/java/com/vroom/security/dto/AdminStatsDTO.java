package com.vroom.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDTO {
    private long totalUsers;
    private long totalStudents;
    private long totalInstructors;
    private long totalAdmins;
    private long activeUsers;
    private long pendingInstructors;

    private Double averageStudentCompletionPercentage;
    private Long totalScenariosCompleted;

    private Double averageInstructorRating;
    private Long totalActiveStudents;
}
