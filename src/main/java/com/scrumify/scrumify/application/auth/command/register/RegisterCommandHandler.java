package com.scrumify.scrumify.application.auth.command.register;

import org.springframework.stereotype.Service;

import com.scrumify.scrumify.application.auth.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegisterCommandHandler {
    private final UserRepository userRepository;
    RegisterCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void Handle(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            log.error("User with email {} already exists", command.email());
            throw new IllegalArgumentException("User with email " + command.email() + " already exists");
        }

        log.info("Registering user with email {}", command.email());
    }
}
