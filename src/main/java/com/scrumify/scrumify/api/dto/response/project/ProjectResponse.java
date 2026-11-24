package com.scrumify.scrumify.api.dto.response.project;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        UUID createdBy,
        String name,
        String codePrefix,
        String description,
        Instant createdAt,
        Instant updatedAt) {
}
