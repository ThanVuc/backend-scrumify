package com.scrumify.scrumify.application.scrum.command.createpbi;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.scrum.query.listpbi.PbiResult;
import com.scrumify.scrumify.application.scrum.repository.BacklogRepository;
import com.scrumify.scrumify.application.scrum.repository.EpicRepository;
import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintPbiRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintRepository;
import com.scrumify.scrumify.domain.entity.epic.Epic;
import com.scrumify.scrumify.domain.entity.pbi.Pbi;
import com.scrumify.scrumify.domain.entity.pbi.PbiPriority;
import com.scrumify.scrumify.domain.entity.pbi.PbiStatus;
import com.scrumify.scrumify.domain.entity.sprint.Sprint;
import com.scrumify.scrumify.domain.entity.sprint.SprintPbi;
import com.scrumify.scrumify.domain.entity.sprint.SprintStatus;
import com.scrumify.scrumify.domain.exception.BacklogNotFoundException;
import com.scrumify.scrumify.domain.exception.EpicNotFoundException;
import com.scrumify.scrumify.domain.exception.ProjectNotFoundException;
import com.scrumify.scrumify.domain.exception.SprintCompletedException;
import com.scrumify.scrumify.domain.exception.SprintNotFoundException;
import com.scrumify.scrumify.domain.exception.UserNotFoundException;

@Service
public class CreatePbiCommandHandler {
    private final CurrentUserService currentUserService;
    private final ProjectRepository projectRepository;
    private final BacklogRepository backlogRepository;
    private final EpicRepository epicRepository;
    private final SprintRepository sprintRepository;
    private final PbiRepository pbiRepository;
    private final SprintPbiRepository sprintPbiRepository;

    public CreatePbiCommandHandler(
            CurrentUserService currentUserService,
            ProjectRepository projectRepository,
            BacklogRepository backlogRepository,
            EpicRepository epicRepository,
            SprintRepository sprintRepository,
            PbiRepository pbiRepository,
            SprintPbiRepository sprintPbiRepository) {
        this.currentUserService = currentUserService;
        this.projectRepository = projectRepository;
        this.backlogRepository = backlogRepository;
        this.epicRepository = epicRepository;
        this.sprintRepository = sprintRepository;
        this.pbiRepository = pbiRepository;
        this.sprintPbiRepository = sprintPbiRepository;
    }

    @Transactional
    public PbiResult handle(CreatePbiCommand command) {
        var claim = currentUserService.getCurrentUser();
        if (claim == null) {
            throw new UserNotFoundException();
        }

        var project = projectRepository.findByIdAndUserIdForUpdate(command.projectId(), claim.id())
                .orElseThrow(ProjectNotFoundException::new);

        var backlog = backlogRepository.findByProjectId(project.getId())
                .orElseThrow(BacklogNotFoundException::new);

        Epic epic = null;
        if (command.epicId() != null) {
            epic = epicRepository.findActiveByIdAndProjectId(command.epicId(), project.getId())
                    .orElseThrow(EpicNotFoundException::new);
        }

        Sprint sprint = null;
        if (command.sprintId() != null) {
            sprint = sprintRepository.findByIdAndProjectId(command.sprintId(), project.getId())
                    .orElseThrow(SprintNotFoundException::new);
            if (sprint.getStatus() == SprintStatus.COMPLETED) {
                throw new SprintCompletedException();
            }
        }

        int issueNumber = project.getNextIssueNumber();
        project.setNextIssueNumber(issueNumber + 1);
        projectRepository.save(project);

        var pbi = new Pbi();
        pbi.setBacklog(backlog);
        pbi.setEpic(epic);
        pbi.setCode(project.getCodePrefix() + "-" + issueNumber);
        pbi.setTitle(command.title().trim());
        pbi.setDescription(command.description());
        pbi.setType(command.type());
        pbi.setStatus(PbiStatus.TODO);
        pbi.setPriority(command.priority() != null ? command.priority() : PbiPriority.MEDIUM);
        pbi.setStoryPoints(command.storyPoints());

        var savedPbi = pbiRepository.save(pbi);

        if (sprint != null) {
            var sprintPbi = new SprintPbi();
            sprintPbi.setSprint(sprint);
            sprintPbi.setPbi(savedPbi);
            sprintPbiRepository.save(sprintPbi);
        }

        return PbiResult.from(savedPbi, sprint);
    }
}
