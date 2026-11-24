package com.scrumify.scrumify.api.dto.response.project;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectDetailResponse(
        UUID id,
        MemberSummary createdBy,
        String name,
        String codePrefix,
        String description,
        List<MemberDetail> members,
        ActiveSprintDetail activeSprint,
        PbiStatistic pbiStatistic,
        Instant createdAt,
        Instant updatedAt) {
    public record MemberSummary(UUID id, UUID userId, String name) {
    }

    public record MemberDetail(UUID id, UUID userId, String name, String role) {
    }

    public record ActiveSprintDetail(UUID id, String name, LocalDate start, LocalDate end) {
    }

    public record PbiStatistic(long total, long backlogTotal, long debtTotal) {
    }
}
