package com.scrumify.scrumify.api.dto.response.project;

import java.util.List;

public record ProjectListResponse(List<ProjectListItem> items) {
    public record ProjectListItem(
            java.util.UUID id,
            String name,
            List<MemberSummary> members,
            ActiveSprintSummary activeSprint,
            long activePBIsCount,
            long totalPBIsCount) {
    }

    public record MemberSummary(java.util.UUID id, java.util.UUID userId, String name) {
    }

    public record ActiveSprintSummary(
            java.util.UUID id,
            String name,
            java.time.LocalDate end) {
    }
}
