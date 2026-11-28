package com.scrumify.scrumify.application.scrum.query.listpbi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.scrum.repository.BacklogRepository;
import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintPbiRepository;
import com.scrumify.scrumify.domain.entity.pbi.Pbi;
import com.scrumify.scrumify.domain.entity.sprint.Sprint;
import com.scrumify.scrumify.domain.entity.sprint.SprintPbi;
import com.scrumify.scrumify.domain.exception.BacklogNotFoundException;
import com.scrumify.scrumify.domain.exception.ProjectNotFoundException;
import com.scrumify.scrumify.domain.exception.UserNotFoundException;

@Service
public class ListPbiQueryHandler {
    private final CurrentUserService currentUserService;
    private final ProjectRepository projectRepository;
    private final BacklogRepository backlogRepository;
    private final PbiRepository pbiRepository;
    private final SprintPbiRepository sprintPbiRepository;

    public ListPbiQueryHandler(
            CurrentUserService currentUserService,
            ProjectRepository projectRepository,
            BacklogRepository backlogRepository,
            PbiRepository pbiRepository,
            SprintPbiRepository sprintPbiRepository) {
        this.currentUserService = currentUserService;
        this.projectRepository = projectRepository;
        this.backlogRepository = backlogRepository;
        this.pbiRepository = pbiRepository;
        this.sprintPbiRepository = sprintPbiRepository;
    }

    @Transactional(readOnly = true)
    public BacklogResult handle(ListPbiQuery query) {
        var claim = currentUserService.getCurrentUser();
        if (claim == null) {
            throw new UserNotFoundException();
        }

        projectRepository.findDetailByIdAndUserId(query.projectId(), claim.id())
                .orElseThrow(ProjectNotFoundException::new);

        var backlog = backlogRepository.findByProjectId(query.projectId())
                .orElseThrow(BacklogNotFoundException::new);

        List<Pbi> pbis = pbiRepository.findActiveByBacklogId(
                backlog.getId(), query.status(), query.type(), query.epicId(), query.search());

        Map<UUID, Sprint> currentSprintByPbiId = new HashMap<>();
        if (!pbis.isEmpty()) {
            var pbiIds = pbis.stream().map(Pbi::getId).toList();
            for (SprintPbi sp : sprintPbiRepository.findCurrentByPbiIdIn(pbiIds)) {
                currentSprintByPbiId.putIfAbsent(sp.getPbi().getId(), sp.getSprint());
            }
        }

        List<PbiResult> pbiResults = pbis.stream()
                .map(pbi -> PbiResult.from(pbi, currentSprintByPbiId.get(pbi.getId())))
                .toList();

        return new BacklogResult(backlog.getId(), query.projectId(), pbiResults);
    }
}
