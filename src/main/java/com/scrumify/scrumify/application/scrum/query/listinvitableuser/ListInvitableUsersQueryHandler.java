package com.scrumify.scrumify.application.scrum.query.listinvitableuser;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumify.scrumify.application.auth.repository.UserRepository;
import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.domain.entity.user.UserStatus;
import com.scrumify.scrumify.domain.exception.ProjectNotFoundException;
import com.scrumify.scrumify.domain.exception.UserNotFoundException;

@Service
public class ListInvitableUsersQueryHandler {
    private final CurrentUserService currentUserService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ListInvitableUsersQueryHandler(
            CurrentUserService currentUserService,
            ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<InvitableUserResult> handle(ListInvitableUsersQuery query) {
        var claim = currentUserService.getCurrentUser();
        if (claim == null) {
            throw new UserNotFoundException();
        }

        projectRepository.findDetailByIdAndUserId(query.projectId(), claim.id())
                .orElseThrow(ProjectNotFoundException::new);

        if (query.emails() == null || query.emails().isEmpty()) {
            return List.of();
        }

        var normalizedEmails = query.emails().stream()
                .map(email -> email.trim().toLowerCase(Locale.ROOT))
                .distinct()
                .toList();

        return userRepository.findAllByEmailInIgnoreCaseAndStatus(normalizedEmails, UserStatus.ACTIVE).stream()
                .map(user -> new InvitableUserResult(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail()))
                .toList();
    }
}
