package com.scrumify.scrumify.application.scrum.query.listinvitableuser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.scrumify.scrumify.application.auth.repository.UserRepository;
import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.common.model.JwtClaim;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.domain.entity.project.Project;
import com.scrumify.scrumify.domain.entity.user.User;
import com.scrumify.scrumify.domain.entity.user.UserRole;
import com.scrumify.scrumify.domain.entity.user.UserStatus;
import com.scrumify.scrumify.domain.exception.ProjectNotFoundException;

class ListInvitableUsersQueryHandlerTests {

    private CurrentUserService currentUserService;
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private ListInvitableUsersQueryHandler handler;

    private User currentUser;
    private Project project;

    @BeforeEach
    void setUp() {
        currentUserService = mock(CurrentUserService.class);
        projectRepository = mock(ProjectRepository.class);
        userRepository = mock(UserRepository.class);
        handler = new ListInvitableUsersQueryHandler(currentUserService, projectRepository, userRepository);

        currentUser = new User("Owner", "owner@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);
        project = new Project(currentUser, "Scrumify", "SCRUM", "desc");
        when(currentUserService.getCurrentUser()).thenReturn(
                new JwtClaim(currentUser.getId(), currentUser.getFullName(), currentUser.getEmail(), "MEMBER"));
        when(projectRepository.findDetailByIdAndUserId(project.getId(), currentUser.getId()))
                .thenReturn(Optional.of(project));
    }

    @Test
    void handle_whenEmailsMissingOrEmpty_returnsEmptyWithoutQueryingUsers() {
        assertThat(handler.handle(new ListInvitableUsersQuery(project.getId(), null))).isEmpty();
        assertThat(handler.handle(new ListInvitableUsersQuery(project.getId(), List.of()))).isEmpty();
        verify(userRepository, never()).findAllByEmailInIgnoreCaseAndStatus(any(), any());
    }

    @Test
    void handle_whenEmailsProvided_normalizesDeduplicatesAndReturnsActiveUsers() {
        User user1 = new User("Alex", "alex@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);
        when(userRepository.findAllByEmailInIgnoreCaseAndStatus(
                eq(List.of("alex@example.com")), eq(UserStatus.ACTIVE)))
                .thenReturn(List.of(user1));

        var results = handler.handle(new ListInvitableUsersQuery(
                project.getId(), List.of("  ALEX@example.com ", "alex@example.com")));

        assertThat(results).containsExactly(
                new InvitableUserResult(user1.getId(), "Alex", "alex@example.com"));
    }

    @Test
    void handle_whenProjectNotFoundOrNotMember_throwsProjectNotFoundException() {
        UUID otherProjectId = UUID.randomUUID();
        when(projectRepository.findDetailByIdAndUserId(otherProjectId, currentUser.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new ListInvitableUsersQuery(otherProjectId, List.of("a@example.com"))))
                .isInstanceOf(ProjectNotFoundException.class);
    }
}
