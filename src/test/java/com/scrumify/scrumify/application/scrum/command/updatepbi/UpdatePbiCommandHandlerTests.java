package com.scrumify.scrumify.application.scrum.command.updatepbi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.common.model.JwtClaim;
import com.scrumify.scrumify.application.scrum.query.listpbi.PbiResult;
import com.scrumify.scrumify.application.scrum.repository.EpicRepository;
import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintPbiRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintRepository;
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
import com.scrumify.scrumify.domain.exception.PbiNotFoundException;

class UpdatePbiCommandHandlerTests {

    private CurrentUserService currentUserService;
    private PbiRepository pbiRepository;
    private ProjectRepository projectRepository;
    private EpicRepository epicRepository;
    private SprintRepository sprintRepository;
    private SprintPbiRepository sprintPbiRepository;
    private UpdatePbiCommandHandler handler;

    private User user;
    private Project project;
    private Backlog backlog;
    private Pbi pbi;

    @BeforeEach
    void setUp() {
        currentUserService = mock(CurrentUserService.class);
        pbiRepository = mock(PbiRepository.class);
        projectRepository = mock(ProjectRepository.class);
        epicRepository = mock(EpicRepository.class);
        sprintRepository = mock(SprintRepository.class);
        sprintPbiRepository = mock(SprintPbiRepository.class);
        handler = new UpdatePbiCommandHandler(
                currentUserService, pbiRepository, projectRepository,
                epicRepository, sprintRepository, sprintPbiRepository);

        user = new User("Owner", "owner@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);
        project = new Project(user, "Scrumify", "SCRUM", "desc");
        backlog = new Backlog();
        backlog.setProject(project);

        Epic epic = new Epic();
        epic.setName("Initial Epic");

        pbi = new Pbi();
        pbi.setBacklog(backlog);
        pbi.setEpic(epic);
        pbi.setCode("SCRUM-1");
        pbi.setTitle("Original");
        pbi.setType(PbiType.FEATURE);
        pbi.setStatus(PbiStatus.TODO);
        pbi.setPriority(PbiPriority.MEDIUM);

        when(currentUserService.getCurrentUser())
                .thenReturn(new JwtClaim(user.getId(), user.getFullName(), user.getEmail(), "MEMBER"));
        when(pbiRepository.findById(pbi.getId())).thenReturn(Optional.of(pbi));
        when(projectRepository.findDetailByIdAndUserId(project.getId(), user.getId()))
                .thenReturn(Optional.of(project));
        when(pbiRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void handle_updatesPartialFieldsClearsNullEpicAndSprintAndMovesToTop() {
        Sprint oldSprint = new Sprint();
        oldSprint.setName("Old Sprint");
        oldSprint.setStatus(SprintStatus.PLANNED);
        SprintPbi oldLink = new SprintPbi();
        oldLink.setSprint(oldSprint);
        oldLink.setPbi(pbi);

        when(sprintPbiRepository.findCurrentByPbiId(pbi.getId())).thenReturn(List.of(oldLink));

        PbiResult result = handler.handle(new UpdatePbiCommand(
                pbi.getId(),
                true, "Updated Title",
                false, null,
                false, null,
                true, PbiPriority.HIGHEST,
                false, null,
                true, null,
                true, PbiStatus.INPROGRESS,
                true, null,
                true,
                null));

        assertThat(result.title()).isEqualTo("Updated Title");
        assertThat(result.priority()).isEqualTo("HIGHEST");
        assertThat(result.status()).isEqualTo("INPROGRESS");
        assertThat(result.epicId()).isNull();
        assertThat(result.sprint()).isNull();
        assertThat(pbi.getMoveToTopAt()).isNotNull();
        verify(sprintPbiRepository).deleteAll(List.of(oldLink));
    }

    @Test
    void handle_archiveIsIdempotentAndArchivedPbiRejectsNormalUpdatesWith404() {
        pbi.setArchivedAt(Instant.parse("2026-11-20T00:00:00Z"));
        when(sprintPbiRepository.findCurrentByPbiId(pbi.getId())).thenReturn(List.of());

        PbiResult archivedResult = handler.handle(new UpdatePbiCommand(
                pbi.getId(), false, null, false, null, false, null, false, null,
                false, null, false, null, false, null, false, null, null, true));

        assertThat(archivedResult.archivedAt()).isEqualTo(Instant.parse("2026-11-20T00:00:00Z"));
        verify(pbiRepository, never()).save(any());

        assertThatThrownBy(() -> handler.handle(new UpdatePbiCommand(
                pbi.getId(), true, "New Title", false, null, false, null, false, null,
                false, null, false, null, false, null, false, null, null, null)))
                .isInstanceOf(PbiNotFoundException.class);
    }
}
