package com.scrumify.scrumify.application.scrum.query.detailproject;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectDetailResult(
        UUID id,
        MemberSummaryResult createdBy,
        String name,
        String codePrefix,
        String description,
        List<MemberDetailResult> members,
        ActiveSprintDetailResult activeSprint,
        PbiStatisticResult pbiStatistic,
        Instant createdAt,
        Instant updatedAt) {
    public record MemberSummaryResult(UUID id, UUID userId, String name) {
    }

    public record MemberDetailResult(UUID id, UUID userId, String name, String role) {
    }

    public record ActiveSprintDetailResult(UUID id, String name, LocalDate start, LocalDate end) {
    }

    public record PbiStatisticResult(long total, long backlogTotal, long debtTotal) {
    }
}
