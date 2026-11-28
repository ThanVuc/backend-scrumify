package com.scrumify.scrumify.application.scrum.query.detailpbi;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.scrumify.scrumify.application.scrum.query.listpbi.PbiResult;

public record TaskItemResult(
        UUID id,
        UUID pbiId,
        String pbiTitle,
        String pbiCode,
        UUID assigneeId,
        String assigneeName,
        PbiResult.SprintSummaryResult sprint,
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
}
