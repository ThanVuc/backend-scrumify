package com.scrumify.scrumify.api.dto.request.pbi;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePbiRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 256, message = "Title must not exceed 256 characters")
        String title,

        String description,

        @NotBlank(message = "Type is required")
        @Pattern(regexp = "^(feature|bug|technical)$", message = "Type must be feature, bug, or technical")
        String type,

        @Pattern(regexp = "^(LOW|MEDIUM|HIGH|HIGHEST)$", message = "Priority must be LOW, MEDIUM, HIGH, or HIGHEST")
        String priority,

        @Min(value = 0, message = "Story points must be greater than or equal to 0")
        Integer storyPoints,

        UUID epicId,

        UUID sprintId) {
}
