package com.scrumify.scrumify.api.dto.request.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 256, message = "Name must not exceed 256 characters")
        String name,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotBlank(message = "Code prefix is required")
        @Size(max = 7, message = "Code prefix must not exceed 7 characters")
        @Pattern(regexp = "[A-Za-z]+", message = "Code prefix must contain only letters")
        String codePrefix) {
}
