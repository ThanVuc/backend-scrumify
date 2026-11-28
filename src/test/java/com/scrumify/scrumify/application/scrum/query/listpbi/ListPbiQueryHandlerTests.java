package com.scrumify.scrumify.application.scrum.query.listpbi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.common.model.JwtClaim;
import com.scrumify.scrumify.application.scrum.repository.BacklogRepository;
import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintPbiRepository;
import com.scrumify.scrumify.domain.entity.backlog.Backlog;
import com.scrumify.scrumify.domain.entity.epic.Epic;
import com.scrumify.scrumify.domain.entity.pbi.Pbi;
import com.scrumify.scrumify.domain.entity.pbi.PbiPriority;
import com.scrumify.scrumify.domain.entity.pbi.PbiStatus;
import com.scrumify.scrumify.domain.entity.pbi.PbiType;
import com.scrumify.scrumify.domain.entity.project.Project;
import com.scrumify.scrumify.domain.entity.sprint.Sprint;
import com.scrumify.scrumify.domain.entity.sprint.SprintPbi;
import com.scrumify.scrumify.domain.entity.sprint.SprintStatus;
import com.scrumify.scrumify.domain.entity.user.User;
import com.scrumify.scrumify.domain.entity.user.UserRole;
import com.scrumify.scrumify.domain.entity.user.UserStatus;
import com.scrumify.scrumify.domain.exception.ProjectNotFoundException;

class ListPbiQueryHandlerTests {

    private CurrentUserService currentUserService;
    private ProjectRepository projectRepository;
    private BacklogRepository backlogRepository;
    private PbiRepository pbiRepository;
    private SprintPbiRepository sprintPbiRepository;
    private ListPbiQueryHandler handler;

    private User user;
    private Project project;
    private Backlog backlog;

    @BeforeEach
    void setUp() {
        currentUserService = mock(CurrentUserService.class);
        projectRepository = mock(ProjectRepository.class);
        backlogRepository = mock(BacklogRepository.class);
        pbiRepository = mock(PbiRepository.class);
        sprintPbiRepository = mock(SprintPbiRepository.class);
        handler = new ListPbiQueryHandler(
                currentUserService, projectRepository, backlogRepository, pbiRepository, sprintPbiRepository);

        user = new User("Member", "member@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);
        project = new Project(user, "Scrumify", "SCRUM", "desc");
        backlog = new Backlog();
        backlog.setProject(project);

        when(currentUserService.getCurrentUser())
                .thenReturn(new JwtClaim(user.getId(), user.getFullName(), user.getEmail(), "MEMBER"));
        when(projectRepository.findDetailByIdAndUserId(project.getId(), user.getId()))
                .thenReturn(Optional.of(project));
        when(backlogRepository.findByProjectId(project.getId())).thenReturn(Optional.of(backlog));
    }

    @Test
    void handle_returnsBacklogWithEpicAndSprintSummaries() {
        Epic epic = new Epic();
        epic.setName("Core Epic");

        Pbi pbi = new Pbi();
        pbi.setBacklog(backlog);
        pbi.setEpic(epic);
        pbi.setCode("SCRUM-1");
        pbi.setTitle("Setup");
        pbi.setType(PbiType.FEATURE);
        pbi.setStatus(PbiStatus.TODO);
        pbi.setPriority(PbiPriority.HIGH);

        Sprint sprint = new Sprint();
        sprint.setName("Sprint 1");
        sprint.setStatus(SprintStatus.ACTIVE);

        SprintPbi sprintPbi = new SprintPbi();
        sprintPbi.setSprint(sprint);
        sprintPbi.setPbi(pbi);

        when(pbiRepository.findActiveByBacklogId(backlog.getId(), PbiStatus.TODO, PbiType.FEATURE, epic.getId(), "set"))
                .thenReturn(List.of(pbi));
        when(sprintPbiRepository.findCurrentByPbiIdIn(any())).thenReturn(List.of(sprintPbi));

        BacklogResult result = handler.handle(new ListPbiQuery(
                project.getId(), PbiStatus.TODO, PbiType.FEATURE, epic.getId(), "set"));

        assertThat(result.id()).isEqualTo(backlog.getId());
        assertThat(result.projectId()).isEqualTo(project.getId());
        assertThat(result.pbis()).hasSize(1);
        PbiResult item = result.pbis().getFirst();
        assertThat(item.code()).isEqualTo("SCRUM-1");
        assertThat(item.type()).isEqualTo("feature");
        assertThat(item.epic().name()).isEqualTo("Core Epic");
        assertThat(item.sprint().name()).isEqualTo("Sprint 1");
        assertThat(item.sprint().status()).isEqualTo("ACTIVE");
    }

    @Test
    void handle_whenNotProjectMember_throwsProjectNotFoundException() {
        UUID otherProjectId = UUID.randomUUID();
        when(projectRepository.findDetailByIdAndUserId(otherProjectId, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new ListPbiQuery(otherProjectId, null, null, null, null)))
                .isInstanceOf(ProjectNotFoundException.class);
    }
}
