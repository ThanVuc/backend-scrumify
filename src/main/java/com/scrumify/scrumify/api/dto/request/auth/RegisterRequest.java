package com.scrumify.scrumify.api.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
    @NotBlank(message = "Name is required")
    @Size(max = 128)
    String name,
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,
    @NotBlank(message = "Password is required")
    @Size(
        min = 8,
        max = 128,
        message = "Password must be between 8 and 128 characters"
    )
    String password,

    @NotBlank(message = "Confirm password is required")
    @Size(
        min = 8,
        max = 128,
        message = "Confirm password must be between 8 and 128 characters"
    )
    String confirmPassword
){}
