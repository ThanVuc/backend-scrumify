package com.scrumify.scrumify.api.mapper;

import com.scrumify.scrumify.api.dto.request.auth.LoginRequest;
import com.scrumify.scrumify.api.dto.request.auth.RegisterRequest;
import com.scrumify.scrumify.api.dto.response.auth.MeResponse;
import com.scrumify.scrumify.application.auth.command.login.LoginCommand;
import com.scrumify.scrumify.application.auth.command.register.RegisterCommand;
import com.scrumify.scrumify.application.auth.query.me.MeQueryResult;
import com.scrumify.scrumify.domain.entity.user.User;

public final class AuthMapper {
    private AuthMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static RegisterCommand toRegisterCommand(RegisterRequest request) {
        return new RegisterCommand(
            request.name(),
            request.email(),
            request.password(),
            request.confirmPassword()
        );
    }

    public static LoginCommand toLoginCommand(LoginRequest request) {
        return new LoginCommand(
            request.email(),
            request.password()
        );
    }

    public static MeResponse toMeResponse(MeQueryResult result) {
        return new MeResponse(
            result.id(),
            result.username(),
            result.email(),
            result.roles(),
            result.status(),
            result.avatarUrl(),
            result.createdAt(),
            result.updatedAt(),
            result.lastLoginAt()
        );
    }
}
