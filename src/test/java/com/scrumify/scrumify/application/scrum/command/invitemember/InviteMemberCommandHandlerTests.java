package com.scrumify.scrumify.application.scrum.command.invitemember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.scrumify.scrumify.application.auth.repository.UserRepository;
import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.common.model.JwtClaim;
import com.scrumify.scrumify.application.scrum.repository.MemberRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.domain.entity.member.Member;
import com.scrumify.scrumify.domain.entity.member.MemberRole;
import com.scrumify.scrumify.domain.entity.project.Project;
import com.scrumify.scrumify.domain.entity.user.User;
import com.scrumify.scrumify.domain.entity.user.UserRole;
import com.scrumify.scrumify.domain.entity.user.UserStatus;
import com.scrumify.scrumify.domain.exception.ProjectNotFoundException;
import com.scrumify.scrumify.domain.exception.UserNotFoundException;

class InviteMemberCommandHandlerTests {

    private CurrentUserService currentUserService;
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private MemberRepository memberRepository;
    private InviteMemberCommandHandler handler;

    private User currentUser;
    private Project project;

    @BeforeEach
    void setUp() {
        currentUserService = mock(CurrentUserService.class);
        projectRepository = mock(ProjectRepository.class);
        userRepository = mock(UserRepository.class);
        memberRepository = mock(MemberRepository.class);
        handler = new InviteMemberCommandHandler(
                currentUserService, projectRepository, userRepository, memberRepository);

        currentUser = new User("Owner", "owner@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);
        project = new Project(currentUser, "Scrumify", "SCRUM", "desc");
        when(currentUserService.getCurrentUser()).thenReturn(
                new JwtClaim(currentUser.getId(), currentUser.getFullName(), currentUser.getEmail(), "MEMBER"));
        when(projectRepository.findDetailByIdAndUserId(project.getId(), currentUser.getId()))
                .thenReturn(Optional.of(project));
    }

    @Test
    void handle_normalizesDeduplicatesSkipsExistingAndInsertsNewMembers() {
        User existingMemberUser = new User("Existing", "existing@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);
        User newUser = new User("New User", "new@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);

        when(userRepository.findAllByEmailInIgnoreCaseAndStatus(
                eq(List.of("existing@example.com", "new@example.com")), eq(UserStatus.ACTIVE)))
                .thenReturn(List.of(existingMemberUser, newUser));
        when(memberRepository.findUserIdsByProjectIdAndUserIdIn(
                eq(project.getId()), anyCollection()))
                .thenReturn(Set.of(existingMemberUser.getId()));
        when(memberRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var results = handler.handle(new InviteMemberCommand(
                project.getId(),
                List.of("  EXISTING@example.com ", "new@example.com", "New@Example.com")));

        assertThat(results).hasSize(1);
        InviteMemberResult result = results.getFirst();
        assertThat(result.userId()).isEqualTo(newUser.getId());
        assertThat(result.name()).isEqualTo("New User");
        assertThat(result.role()).isEqualTo("member");
        assertThat(result.id()).isNotNull();
    }

    @Test
    void handle_whenAllUsersAlreadyMembers_returnsEmptyAndSkipsSaveAll() {
        User existingMemberUser = new User("Existing", "existing@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);

        when(userRepository.findAllByEmailInIgnoreCaseAndStatus(
                eq(List.of("existing@example.com")), eq(UserStatus.ACTIVE)))
                .thenReturn(List.of(existingMemberUser));
        when(memberRepository.findUserIdsByProjectIdAndUserIdIn(
                eq(project.getId()), anyCollection()))
                .thenReturn(Set.of(existingMemberUser.getId()));

        var results = handler.handle(new InviteMemberCommand(
                project.getId(), List.of("existing@example.com")));

        assertThat(results).isEmpty();
        verify(memberRepository, never()).saveAll(any());
    }

    @Test
    void handle_whenEmailNotFoundOrDisabled_throwsUserNotFoundException() {
        User activeUser = new User("Active", "active@example.com", "hash", UserRole.MEMBER, UserStatus.ACTIVE, null);
        when(userRepository.findAllByEmailInIgnoreCaseAndStatus(
                eq(List.of("active@example.com", "missing@example.com")), eq(UserStatus.ACTIVE)))
                .thenReturn(List.of(activeUser));

        assertThatThrownBy(() -> handler.handle(new InviteMemberCommand(
                project.getId(), List.of("active@example.com", "missing@example.com"))))
                .isInstanceOf(UserNotFoundException.class);

        verify(memberRepository, never()).saveAll(any());
    }

    @Test
    void handle_whenProjectNotFoundOrNotMember_throwsProjectNotFoundException() {
        UUID otherProjectId = UUID.randomUUID();
        when(projectRepository.findDetailByIdAndUserId(otherProjectId, currentUser.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new InviteMemberCommand(
                otherProjectId, List.of("a@example.com"))))
                .isInstanceOf(ProjectNotFoundException.class);
    }
}
