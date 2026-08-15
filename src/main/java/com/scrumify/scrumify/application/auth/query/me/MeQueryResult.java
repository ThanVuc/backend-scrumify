package com.scrumify.scrumify.application.auth.query.me;

import java.time.Instant;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.user.UserRole;
import com.scrumify.scrumify.domain.entity.user.UserStatus;

public record MeQueryResult(
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