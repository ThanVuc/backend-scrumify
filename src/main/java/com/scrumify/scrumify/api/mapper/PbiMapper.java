package com.scrumify.scrumify.api.mapper;

import java.util.Locale;
import java.util.UUID;

import com.scrumify.scrumify.api.dto.request.pbi.CreatePbiRequest;
import com.scrumify.scrumify.api.dto.request.pbi.UpdatePbiRequest;
import com.scrumify.scrumify.api.dto.response.pbi.BacklogResponse;
import com.scrumify.scrumify.api.dto.response.pbi.PbiDetailResponse;
import com.scrumify.scrumify.api.dto.response.pbi.PbiResponse;
import com.scrumify.scrumify.api.dto.response.pbi.TaskResponse;
import com.scrumify.scrumify.application.scrum.command.createpbi.CreatePbiCommand;
import com.scrumify.scrumify.application.scrum.command.updatepbi.UpdatePbiCommand;
import com.scrumify.scrumify.application.scrum.query.detailpbi.PbiDetailResult;
import com.scrumify.scrumify.application.scrum.query.detailpbi.TaskItemResult;
import com.scrumify.scrumify.application.scrum.query.listpbi.BacklogResult;
import com.scrumify.scrumify.application.scrum.query.listpbi.ListPbiQuery;
import com.scrumify.scrumify.application.scrum.query.listpbi.PbiResult;
import com.scrumify.scrumify.domain.entity.pbi.PbiPriority;
import com.scrumify.scrumify.domain.entity.pbi.PbiStatus;
import com.scrumify.scrumify.domain.entity.pbi.PbiType;

public final class PbiMapper {
    private PbiMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static ListPbiQuery toListPbiQuery(
            UUID projectId, String status, String type, UUID epicId, String search) {
        return new ListPbiQuery(
                projectId,
                status == null ? null : PbiStatus.valueOf(status),
                type == null ? null : PbiType.valueOf(type.toUpperCase(Locale.ROOT)),
                epicId,
                search);
    }

    public static CreatePbiCommand toCreatePbiCommand(UUID projectId, CreatePbiRequest request) {
        return new CreatePbiCommand(
                projectId,
                request.title(),
                request.description(),
                PbiType.valueOf(request.type().toUpperCase(Locale.ROOT)),
                request.priority() == null ? null : PbiPriority.valueOf(request.priority()),
                request.storyPoints(),
                request.epicId(),
                request.sprintId());
    }

    public static UpdatePbiCommand toUpdatePbiCommand(UUID pbiId, UpdatePbiRequest request) {
        return new UpdatePbiCommand(
                pbiId,
                request.isTitlePresent(),
                request.getTitle(),
                request.isDescriptionPresent(),
                request.getDescription(),
                request.isTypePresent(),
                request.isTypePresent() && request.getType() != null
                        ? PbiType.valueOf(request.getType().toUpperCase(Locale.ROOT))
                        : null,
                request.isPriorityPresent(),
                request.isPriorityPresent() && request.getPriority() != null
                        ? PbiPriority.valueOf(request.getPriority())
                        : null,
                request.isStoryPointsPresent(),
                request.getStoryPoints(),
                request.isEpicIdPresent(),
                request.getEpicId(),
                request.isStatusPresent(),
                request.isStatusPresent() && request.getStatus() != null
                        ? PbiStatus.valueOf(request.getStatus())
                        : null,
                request.isSprintIdPresent(),
                request.getSprintId(),
                request.getMoveToTop(),
                request.getArchived());
    }

    public static BacklogResponse toBacklogResponse(BacklogResult result) {
        var pbis = result.pbis().stream()
                .map(PbiMapper::toPbiResponse)
                .toList();
        return new BacklogResponse(result.id(), result.projectId(), pbis);
    }

    public static PbiDetailResponse toPbiDetailResponse(PbiDetailResult result) {
        var tasks = result.tasks().stream()
                .map(PbiMapper::toTaskResponse)
                .toList();
        return new PbiDetailResponse(toPbiResponse(result.pbi()), tasks);
    }

    public static PbiResponse toPbiResponse(PbiResult result) {
        var epic = result.epic() == null
                ? null
                : new PbiResponse.EpicSummary(result.epic().id(), result.epic().name());
        var sprint = toSprintSummary(result.sprint());
        return new PbiResponse(
                result.id(),
                result.code(),
                result.backlogId(),
                result.epicId(),
                epic,
                sprint,
                result.title(),
                result.description(),
                result.type(),
                result.status(),
                result.priority(),
                result.storyPoints(),
                result.createdAt(),
                result.updatedAt(),
                result.archivedAt());
    }

    private static TaskResponse toTaskResponse(TaskItemResult task) {
        var pbiSummary = new TaskResponse.PbiSummary(task.pbiId(), task.pbiTitle(), task.pbiCode());
        var assignee = task.assigneeId() == null
                ? null
                : new TaskResponse.UserSummary(task.assigneeId(), task.assigneeName());
        return new TaskResponse(
                task.id(),
                pbiSummary,
                assignee,
                toSprintSummary(task.sprint()),
                task.title(),
                task.description(),
                task.status(),
                task.isDebt(),
                task.startDate(),
                task.dueDate(),
                task.priority(),
                task.position(),
                task.estimatedTimeHour(),
                task.createdAt(),
                task.updatedAt(),
                task.archivedAt());
    }

    private static PbiResponse.SprintSummary toSprintSummary(PbiResult.SprintSummaryResult sprint) {
        return sprint == null
                ? null
                : new PbiResponse.SprintSummary(sprint.id(), sprint.name(), sprint.status());
    }
}
