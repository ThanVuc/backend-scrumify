package com.scrumify.scrumify.api.dto.request.member;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record InviteMemberRequest(
        @NotEmpty(message = "Members list is required")
        List<
            @NotBlank(message = "Email is required")
            @Email(message = "Email format is invalid")
            @Size(max = 256, message = "Email must not exceed 256 characters")
            String> members) {
}
