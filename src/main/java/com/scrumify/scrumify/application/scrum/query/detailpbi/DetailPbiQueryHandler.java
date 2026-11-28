package com.scrumify.scrumify.application.scrum.query.detailpbi;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.scrum.query.listpbi.PbiResult;
import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintPbiRepository;
import com.scrumify.scrumify.application.scrum.repository.TaskRepository;
import com.scrumify.scrumify.domain.entity.sprint.Sprint;
import com.scrumify.scrumify.domain.exception.PbiNotFoundException;
import com.scrumify.scrumify.domain.exception.UserNotFoundException;

@Service
public class DetailPbiQueryHandler {
    private final CurrentUserService currentUserService;
    private final PbiRepository pbiRepository;
    private final ProjectRepository projectRepository;
    private final SprintPbiRepository sprintPbiRepository;
    private final TaskRepository taskRepository;

    public DetailPbiQueryHandler(
            CurrentUserService currentUserService,
            PbiRepository pbiRepository,
            ProjectRepository projectRepository,
            SprintPbiRepository sprintPbiRepository,
            TaskRepository taskRepository) {
        this.currentUserService = currentUserService;
        this.pbiRepository = pbiRepository;
        this.projectRepository = projectRepository;
        this.sprintPbiRepository = sprintPbiRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public PbiDetailResult handle(DetailPbiQuery query) {
        var claim = currentUserService.getCurrentUser();
        if (claim == null) {
            throw new UserNotFoundException();
        }

        var pbi = pbiRepository.findActiveById(query.pbiId())
                .orElseThrow(PbiNotFoundException::new);

        var projectId = pbi.getBacklog().getProject().getId();
        projectRepository.findDetailByIdAndUserId(projectId, claim.id())
                .orElseThrow(PbiNotFoundException::new);

        var currentSprintPbis = sprintPbiRepository.findCurrentByPbiId(pbi.getId());
        Sprint currentSprint = currentSprintPbis.isEmpty() ? null : currentSprintPbis.getFirst().getSprint();

        var pbiResult = PbiResult.from(pbi, currentSprint);
        var sprintSummary = pbiResult.sprint();

        var tasks = taskRepository.findActiveByPbiIdOrderByPositionAsc(pbi.getId()).stream()
                .map(task -> new TaskItemResult(
                        task.getId(),
                        pbi.getId(),
                        pbi.getTitle(),
                        pbi.getCode(),
                        task.getAssignee() == null ? null : task.getAssignee().getId(),
                        task.getAssignee() == null ? null : task.getAssignee().getFullName(),
                        sprintSummary,
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus().name().toLowerCase(Locale.ROOT),
                        task.isDebt(),
                        task.getStartDate(),
                        task.getDueDate(),
                        task.getPriority().name(),
                        task.getPosition(),
                        task.getEstimatedTimeHour(),
                        task.getCreatedAt(),
                        task.getUpdatedAt(),
                        task.getArchivedAt()))
                .toList();

        return new PbiDetailResult(pbiResult, tasks);
    }
}
