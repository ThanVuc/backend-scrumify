package com.scrumify.scrumify.api.dto.response.auth;

import java.time.Instant;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.user.UserRole;
import com.scrumify.scrumify.domain.entity.user.UserStatus;

public record MeResponse(
    UUID id,
    String username,
    String email,
    UserRole roles,
    UserStatus status,
    String avatarUrl,
    Instant createdAt,
    Instant updatedAt,
    Instant lastLoginAt
) {}