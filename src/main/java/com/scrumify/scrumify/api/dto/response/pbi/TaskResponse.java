package com.scrumify.scrumify.api.dto.response.pbi;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        PbiSummary pbi,
        UserSummary assignee,
        PbiResponse.SprintSummary sprint,
        String title,
        String description,
        String status,
        boolean isDebt,
        LocalDate startDate,
        LocalDate dueDate,
        String priority,
        Integer position,
        BigDecimal estimatedTimeHour,
        Instant createdAt,
        Instant updatedAt,
        Instant archivedAt) {

    public record PbiSummary(UUID id, String name, String code) {
    }

    public record UserSummary(UUID id, String name) {
    }
}
