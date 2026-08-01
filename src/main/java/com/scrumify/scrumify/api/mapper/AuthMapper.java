package com.scrumify.scrumify.api.mapper;

import com.scrumify.scrumify.api.dto.request.RegisterRequest;
import com.scrumify.scrumify.application.auth.command.register.RegisterCommand;

public final class AuthMapper {
    private AuthMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static RegisterCommand toRegisterCommand(RegisterRequest request) {
        return new RegisterCommand(
            request.name(),
            request.email(),
            request.password()
        );
    }
}
