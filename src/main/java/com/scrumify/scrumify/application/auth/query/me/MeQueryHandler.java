package com.scrumify.scrumify.application.auth.query.me;

import org.springframework.stereotype.Service;

import com.scrumify.scrumify.application.auth.repository.UserRepository;
import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.domain.entity.user.exception.UserNotFoundException;

@Service
public class MeQueryHandler {
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public MeQueryHandler(
            CurrentUserService currentUserService,
            UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    public MeQueryResult Handle() {
        var currentUser = currentUserService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException();
        }

        var user = userRepository.findById(currentUser.id())
                .orElseThrow(UserNotFoundException::new);

        return new MeQueryResult(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRoles(),
                user.getStatus(),
                user.getAvatarUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt());
    }
}