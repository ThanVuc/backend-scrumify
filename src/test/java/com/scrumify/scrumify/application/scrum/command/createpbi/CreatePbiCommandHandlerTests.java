package com.scrumify.scrumify.application.scrum.command.createpbi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.common.model.JwtClaim;
import com.scrumify.scrumify.application.scrum.query.listpbi.PbiResult;
import com.scrumify.scrumify.application.scrum.repository.BacklogRepository;
import com.scrumify.scrumify.application.scrum.repository.EpicRepository;
import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintPbiRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintRepository;
import com.scrumify.scrumify.domain.entity.backlog.Backlog;
import com.scrumify.scrumify.domain.entity.epic.Epic;
import com.scrumify.scrumify.domain.entity.pbi.PbiPriority;
import com.scrumify.scrumify.domain.entity.pbi.PbiType;
import com.scrumify.scrumify.domain.entity.project.Project;
import com.scrumify.scrumify.domain.entity.sprint.Sprint;
import com.scrumify.scrumify.domain.entity.sprint.SprintStatus;
import com.scrumify.scrumify.domain.entity.user.User;
import com.scrumify.scrumify.domain.entity.user.UserRole;
import com.scrumify.scrumify.domain.entity.user.UserStatus;
import com.scrumify.scrumify.domain.exception.SprintCompletedException;

class CreatePbiCommandHandlerTests {

    private CurrentUserService currentUserService;
    private ProjectRepository projectRepository;
    private BacklogRepository backlogRepository;
    private EpicRepository epicRepository;
    private SprintRepository sprintRepository;
    private PbiRepository pbiRepository;
    private SprintPbiRepository sprintPbiRepository;
    private CreatePbiCommandHandler handler;

    private User user;
    private Project project;
    private Backlog backlog;

    @BeforeEach
    void setUp() {
        currentUserService = mock(CurrentUserService.class);
        projectRepository = mock(ProjectRepository.class);
        backlogRepository = mock(BacklogRepository.class);
        epicRepository = mock(EpicRepository.class);
        sprintRepository = mock(SprintRepository.class);
        pbiRepository = mock(PbiRepository.class);
        sprintPbiRepository = mock(SprintPbiRepository.class);
        handler = new CreatePbiCommandHandler(
                currentUserService, projectRepository, backlogRepository,
                epicRepository, sprintRepository, pbiRepository, sprintPbiRepository);

        user = new User("Owner", "owner@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);
        project = new Project(user, "Scrumify", "SCRUM", "desc");
        project.setNextIssueNumber(5);
        backlog = new Backlog();
        backlog.setProject(project);

        when(currentUserService.getCurrentUser())
                .thenReturn(new JwtClaim(user.getId(), user.getFullName(), user.getEmail(), "MEMBER"));
        when(projectRepository.findByIdAndUserIdForUpdate(project.getId(), user.getId()))
                .thenReturn(Optional.of(project));
        when(backlogRepository.findByProjectId(project.getId())).thenReturn(Optional.of(backlog));
        when(pbiRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void handle_incrementsIssueNumberDefaultsPriorityAndLinksEpicAndSprint() {
        Epic epic = new Epic();
        epic.setName("Epic 1");
        Sprint sprint = new Sprint();
        sprint.setName("Sprint 1");
        sprint.setStatus(SprintStatus.PLANNED);

        when(epicRepository.findActiveByIdAndProjectId(epic.getId(), project.getId()))
                .thenReturn(Optional.of(epic));
        when(sprintRepository.findByIdAndProjectId(sprint.getId(), project.getId()))
                .thenReturn(Optional.of(sprint));

        PbiResult result = handler.handle(new CreatePbiCommand(
                project.getId(), "New PBI", "Desc", PbiType.FEATURE, null, 5, epic.getId(), sprint.getId()));

        assertThat(result.code()).isEqualTo("SCRUM-5");
        assertThat(project.getNextIssueNumber()).isEqualTo(6);
        assertThat(result.status()).isEqualTo("TODO");
        assertThat(result.priority()).isEqualTo(PbiPriority.MEDIUM.name());
        assertThat(result.type()).isEqualTo("feature");
        assertThat(result.epic().id()).isEqualTo(epic.getId());
        assertThat(result.sprint().id()).isEqualTo(sprint.getId());
        verify(sprintPbiRepository).save(any());
    }

    @Test
    void handle_whenSprintCompleted_throwsSprintCompletedException() {
        Sprint sprint = new Sprint();
        sprint.setStatus(SprintStatus.COMPLETED);
        when(sprintRepository.findByIdAndProjectId(sprint.getId(), project.getId()))
                .thenReturn(Optional.of(sprint));

        assertThatThrownBy(() -> handler.handle(new CreatePbiCommand(
                project.getId(), "New PBI", null, PbiType.BUG, PbiPriority.HIGH, null, null, sprint.getId())))
                .isInstanceOf(SprintCompletedException.class);
    }
}
