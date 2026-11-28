package com.scrumify.scrumify.application.scrum.query.detailpbi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.common.model.JwtClaim;
import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintPbiRepository;
import com.scrumify.scrumify.application.scrum.repository.TaskRepository;
import com.scrumify.scrumify.domain.entity.backlog.Backlog;
import com.scrumify.scrumify.domain.entity.pbi.Pbi;
import com.scrumify.scrumify.domain.entity.pbi.PbiPriority;
import com.scrumify.scrumify.domain.entity.pbi.PbiStatus;
import com.scrumify.scrumify.domain.entity.pbi.PbiType;
import com.scrumify.scrumify.domain.entity.project.Project;
import com.scrumify.scrumify.domain.entity.task.Task;
import com.scrumify.scrumify.domain.entity.task.TaskPriority;
import com.scrumify.scrumify.domain.entity.task.TaskStatus;
import com.scrumify.scrumify.domain.entity.user.User;
import com.scrumify.scrumify.domain.entity.user.UserRole;
import com.scrumify.scrumify.domain.entity.user.UserStatus;
import com.scrumify.scrumify.domain.exception.PbiNotFoundException;

class DetailPbiQueryHandlerTests {

    private CurrentUserService currentUserService;
    private PbiRepository pbiRepository;
    private ProjectRepository projectRepository;
    private SprintPbiRepository sprintPbiRepository;
    private TaskRepository taskRepository;
    private DetailPbiQueryHandler handler;

    private User user;
    private Project project;
    private Backlog backlog;
    private Pbi pbi;

    @BeforeEach
    void setUp() {
        currentUserService = mock(CurrentUserService.class);
        pbiRepository = mock(PbiRepository.class);
        projectRepository = mock(ProjectRepository.class);
        sprintPbiRepository = mock(SprintPbiRepository.class);
        taskRepository = mock(TaskRepository.class);
        handler = new DetailPbiQueryHandler(
                currentUserService, pbiRepository, projectRepository, sprintPbiRepository, taskRepository);

        user = new User("Alex", "alex@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);
        project = new Project(user, "Scrumify", "SCRUM", "desc");
        backlog = new Backlog();
        backlog.setProject(project);
        pbi = new Pbi();
        pbi.setBacklog(backlog);
        pbi.setCode("SCRUM-1");
        pbi.setTitle("PBI 1");
        pbi.setType(PbiType.BUG);
        pbi.setStatus(PbiStatus.INPROGRESS);
        pbi.setPriority(PbiPriority.HIGHEST);

        when(currentUserService.getCurrentUser())
                .thenReturn(new JwtClaim(user.getId(), user.getFullName(), user.getEmail(), "MEMBER"));
    }

    @Test
    void handle_returnsPbiDetailWithOrderedTasks() {
        Task task = new Task();
        task.setTitle("Fix bug");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
        task.setPosition(0);
        task.setAssignee(user);

        when(pbiRepository.findActiveById(pbi.getId())).thenReturn(Optional.of(pbi));
        when(projectRepository.findDetailByIdAndUserId(project.getId(), user.getId()))
                .thenReturn(Optional.of(project));
        when(sprintPbiRepository.findCurrentByPbiId(pbi.getId())).thenReturn(List.of());
        when(taskRepository.findActiveByPbiIdOrderByPositionAsc(pbi.getId())).thenReturn(List.of(task));

        PbiDetailResult result = handler.handle(new DetailPbiQuery(pbi.getId()));

        assertThat(result.pbi().id()).isEqualTo(pbi.getId());
        assertThat(result.pbi().type()).isEqualTo("bug");
        assertThat(result.tasks()).hasSize(1);
        assertThat(result.tasks().getFirst().title()).isEqualTo("Fix bug");
        assertThat(result.tasks().getFirst().assigneeName()).isEqualTo("Alex");
    }

    @Test
    void handle_whenPbiMissingOrNotMember_throwsPbiNotFoundException() {
        UUID missingId = UUID.randomUUID();
        when(pbiRepository.findActiveById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new DetailPbiQuery(missingId)))
                .isInstanceOf(PbiNotFoundException.class);

        when(pbiRepository.findActiveById(pbi.getId())).thenReturn(Optional.of(pbi));
        when(projectRepository.findDetailByIdAndUserId(project.getId(), user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new DetailPbiQuery(pbi.getId())))
                .isInstanceOf(PbiNotFoundException.class);
    }
}
