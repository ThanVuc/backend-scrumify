package com.scrumify.scrumify.application.scrum.command.updatepbi;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.scrum.query.listpbi.PbiResult;
import com.scrumify.scrumify.application.scrum.repository.EpicRepository;
import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintPbiRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintRepository;
import com.scrumify.scrumify.domain.entity.sprint.Sprint;
import com.scrumify.scrumify.domain.entity.sprint.SprintPbi;
import com.scrumify.scrumify.domain.entity.sprint.SprintStatus;
import com.scrumify.scrumify.domain.exception.EpicNotFoundException;
import com.scrumify.scrumify.domain.exception.PbiNotFoundException;
import com.scrumify.scrumify.domain.exception.SprintCompletedException;
import com.scrumify.scrumify.domain.exception.SprintNotFoundException;
import com.scrumify.scrumify.domain.exception.UserNotFoundException;

@Service
public class UpdatePbiCommandHandler {
    private final CurrentUserService currentUserService;
    private final PbiRepository pbiRepository;
    private final ProjectRepository projectRepository;
    private final EpicRepository epicRepository;
    private final SprintRepository sprintRepository;
    private final SprintPbiRepository sprintPbiRepository;

    public UpdatePbiCommandHandler(
            CurrentUserService currentUserService,
            PbiRepository pbiRepository,
            ProjectRepository projectRepository,
            EpicRepository epicRepository,
            SprintRepository sprintRepository,
            SprintPbiRepository sprintPbiRepository) {
        this.currentUserService = currentUserService;
        this.pbiRepository = pbiRepository;
        this.projectRepository = projectRepository;
        this.epicRepository = epicRepository;
        this.sprintRepository = sprintRepository;
        this.sprintPbiRepository = sprintPbiRepository;
    }

    @Transactional
    public PbiResult handle(UpdatePbiCommand command) {
        var claim = currentUserService.getCurrentUser();
        if (claim == null) {
            throw new UserNotFoundException();
        }

        var pbi = pbiRepository.findById(command.pbiId())
                .orElseThrow(PbiNotFoundException::new);

        var projectId = pbi.getBacklog().getProject().getId();
        projectRepository.findDetailByIdAndUserId(projectId, claim.id())
                .orElseThrow(PbiNotFoundException::new);

        List<SprintPbi> currentSprintPbis = sprintPbiRepository.findCurrentByPbiId(pbi.getId());
        Sprint currentSprint = currentSprintPbis.isEmpty() ? null : currentSprintPbis.getFirst().getSprint();

        if (Boolean.TRUE.equals(command.archived())) {
            if (pbi.getArchivedAt() == null) {
                pbi.setArchivedAt(Instant.now());
                pbi.touchUpdatedAt();
                pbi = pbiRepository.save(pbi);
            }
            return PbiResult.from(pbi, currentSprint);
        }

        if (pbi.getArchivedAt() != null) {
            throw new PbiNotFoundException();
        }

        if (command.epicIdPresent()) {
            if (command.epicId() == null) {
                pbi.setEpic(null);
            } else {
                var epic = epicRepository.findActiveByIdAndProjectId(command.epicId(), projectId)
                        .orElseThrow(EpicNotFoundException::new);
                pbi.setEpic(epic);
            }
        }

        Sprint resultSprint = currentSprint;
        if (command.sprintIdPresent()) {
            if (command.sprintId() == null) {
                if (!currentSprintPbis.isEmpty()) {
                    sprintPbiRepository.deleteAll(currentSprintPbis);
                }
                resultSprint = null;
            } else {
                var targetSprint = sprintRepository.findByIdAndProjectId(command.sprintId(), projectId)
                        .orElseThrow(SprintNotFoundException::new);
                if (targetSprint.getStatus() == SprintStatus.COMPLETED) {
                    throw new SprintCompletedException();
                }

                boolean alreadyLinked = currentSprintPbis.stream()
                        .anyMatch(sp -> sp.getSprint().getId().equals(targetSprint.getId()));
                List<SprintPbi> toRemove = currentSprintPbis.stream()
                        .filter(sp -> !sp.getSprint().getId().equals(targetSprint.getId()))
                        .toList();
                if (!toRemove.isEmpty()) {
                    sprintPbiRepository.deleteAll(toRemove);
                }
                if (!alreadyLinked) {
                    var sprintPbi = new SprintPbi();
                    sprintPbi.setSprint(targetSprint);
                    sprintPbi.setPbi(pbi);
                    sprintPbiRepository.save(sprintPbi);
                }
                resultSprint = targetSprint;
            }
        }

        if (command.titlePresent()) {
            pbi.setTitle(command.title().trim());
        }
        if (command.descriptionPresent()) {
            pbi.setDescription(command.description());
        }
        if (command.typePresent()) {
            pbi.setType(command.type());
        }
        if (command.priorityPresent()) {
            pbi.setPriority(command.priority());
        }
        if (command.storyPointsPresent()) {
            pbi.setStoryPoints(command.storyPoints());
        }
        if (command.statusPresent()) {
            pbi.setStatus(command.status());
        }
        if (Boolean.TRUE.equals(command.moveToTop())) {
            pbi.setMoveToTopAt(Instant.now());
        }

        pbi.touchUpdatedAt();
        var savedPbi = pbiRepository.save(pbi);
        return PbiResult.from(savedPbi, resultSprint);
    }
}
