package com.scrumify.scrumify.application.scrum.query.listproject;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectListItemResult(
        UUID id,
        String name,
        String description,
        List<MemberSummaryResult> members,
        ActiveSprintSummaryResult activeSprint,
        long activePBIsCount,
        long totalPBIsCount) {
    public record MemberSummaryResult(UUID id, UUID userId, String name) {
    }

    public record ActiveSprintSummaryResult(UUID id, String name, LocalDate end) {
    }
}
