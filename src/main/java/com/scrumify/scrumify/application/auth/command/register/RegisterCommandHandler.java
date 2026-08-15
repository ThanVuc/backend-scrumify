package com.scrumify.scrumify.application.auth.command.register;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scrumify.scrumify.application.auth.repository.UserRepository;
import com.scrumify.scrumify.application.common.interfaces.JwtService;
import com.scrumify.scrumify.application.common.model.JwtClaim;
import com.scrumify.scrumify.domain.entity.user.User;
import com.scrumify.scrumify.domain.entity.user.UserRole;
import com.scrumify.scrumify.domain.entity.user.UserStatus;
import com.scrumify.scrumify.domain.entity.user.exception.PasswordAndConfirmNotMatch;
import com.scrumify.scrumify.domain.exception.EmailAlreadyExistsException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegisterCommandHandler {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    RegisterCommandHandler(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String Handle(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException();
        }

        if (!command.password().equals(command.confirmPassword())) {
            throw new PasswordAndConfirmNotMatch();
        }

        var passwordHash = passwordEncoder.encode(command.password());
        var user = new User(
            command.name(),
            command.email(),
            passwordHash,
            UserRole.MEMBER,
            UserStatus.ACTIVE,
            ""
        );

        userRepository.save(user);

        var jwtClaim = new JwtClaim(user.getId(), user.getFullName(), user.getEmail(), user.getRoles().name());
        var accessToken = jwtService.generateAccessToken(jwtClaim);

        return accessToken;
    }
}
