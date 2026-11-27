package com.scrumify.scrumify.application.scrum.command.invitemember;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumify.scrumify.application.auth.repository.UserRepository;
import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.scrum.repository.MemberRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.domain.entity.member.Member;
import com.scrumify.scrumify.domain.entity.member.MemberRole;
import com.scrumify.scrumify.domain.entity.user.User;
import com.scrumify.scrumify.domain.entity.user.UserStatus;
import com.scrumify.scrumify.domain.exception.ProjectNotFoundException;
import com.scrumify.scrumify.domain.exception.UserNotFoundException;

@Service
public class InviteMemberCommandHandler {
    private final CurrentUserService currentUserService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public InviteMemberCommandHandler(
            CurrentUserService currentUserService,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            MemberRepository memberRepository) {
        this.currentUserService = currentUserService;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public List<InviteMemberResult> handle(InviteMemberCommand command) {
        var claim = currentUserService.getCurrentUser();
        if (claim == null) {
            throw new UserNotFoundException();
        }

        var project = projectRepository.findDetailByIdAndUserId(command.projectId(), claim.id())
                .orElseThrow(ProjectNotFoundException::new);

        var normalizedEmails = command.emails().stream()
                .map(email -> email.trim().toLowerCase(Locale.ROOT))
                .distinct()
                .toList();

        var activeUsers = userRepository.findAllByEmailInIgnoreCaseAndStatus(normalizedEmails, UserStatus.ACTIVE);
        Map<String, User> usersByEmail = activeUsers.stream()
                .collect(Collectors.toMap(
                        user -> user.getEmail().toLowerCase(Locale.ROOT),
                        Function.identity(),
                        (first, second) -> first));

        List<User> matchedUsers = new ArrayList<>(normalizedEmails.size());
        for (String email : normalizedEmails) {
            User user = usersByEmail.get(email);
            if (user == null) {
                throw new UserNotFoundException();
            }
            matchedUsers.add(user);
        }

        var matchedUserIds = matchedUsers.stream()
                .map(User::getId)
                .toList();
        var existingUserIds = memberRepository.findUserIdsByProjectIdAndUserIdIn(project.getId(), matchedUserIds);

        List<Member> newMembers = new ArrayList<>();
        for (User user : matchedUsers) {
            if (!existingUserIds.contains(user.getId())) {
                var member = new Member();
                member.setProject(project);
                member.setUser(user);
                member.setRole(MemberRole.MEMBER);
                newMembers.add(member);
            }
        }

        if (newMembers.isEmpty()) {
            return List.of();
        }

        return memberRepository.saveAll(newMembers).stream()
                .map(member -> new InviteMemberResult(
                        member.getId(),
                        member.getUser().getId(),
                        member.getUser().getFullName(),
                        member.getRole().name().toLowerCase(Locale.ROOT)))
                .toList();
    }
}
