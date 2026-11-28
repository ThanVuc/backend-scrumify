package com.scrumify.scrumify.application.scrum.query.listpbi;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.pbi.Pbi;
import com.scrumify.scrumify.domain.entity.sprint.Sprint;

public record PbiResult(
        UUID id,
        String code,
        UUID backlogId,
        UUID epicId,
        EpicSummaryResult epic,
        SprintSummaryResult sprint,
        String title,
        String description,
        String type,
        String status,
        String priority,
        Integer storyPoints,
        Instant createdAt,
        Instant updatedAt,
        Instant archivedAt) {

    public record EpicSummaryResult(UUID id, String name) {
    }

    public record SprintSummaryResult(UUID id, String name, String status) {
    }

    public static PbiResult from(Pbi pbi, Sprint sprint) {
        EpicSummaryResult epicSummary = pbi.getEpic() == null
                ? null
                : new EpicSummaryResult(pbi.getEpic().getId(), pbi.getEpic().getName());
        SprintSummaryResult sprintSummary = sprint == null
                ? null
                : new SprintSummaryResult(sprint.getId(), sprint.getName(), sprint.getStatus().name());
        return new PbiResult(
                pbi.getId(),
                pbi.getCode(),
                pbi.getBacklog().getId(),
                pbi.getEpic() == null ? null : pbi.getEpic().getId(),
                epicSummary,
                sprintSummary,
                pbi.getTitle(),
                pbi.getDescription(),
                pbi.getType().name().toLowerCase(Locale.ROOT),
                pbi.getStatus().name(),
                pbi.getPriority().name(),
                pbi.getStoryPoints(),
                pbi.getCreatedAt(),
                pbi.getUpdatedAt(),
                pbi.getArchivedAt());
    }
}
