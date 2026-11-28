package com.scrumify.scrumify.api.dto.response.pbi;

import java.time.Instant;
import java.util.UUID;

public record PbiResponse(
        UUID id,
        String code,
        UUID backlogId,
        UUID epicId,
        EpicSummary epic,
        SprintSummary sprint,
        String title,
        String description,
        String type,
        String status,
        String priority,
        Integer storyPoints,
        Instant createdAt,
        Instant updatedAt,
        Instant archivedAt) {

    public record EpicSummary(UUID id, String name) {
    }

    public record SprintSummary(UUID id, String name, String status) {
    }
}
