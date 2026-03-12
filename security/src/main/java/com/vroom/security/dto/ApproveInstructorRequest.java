package com.vroom.security.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveInstructorRequest {
    @NotNull
    private Boolean enabled;
}
