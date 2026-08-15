package com.scrumify.scrumify.application.auth.command.register;

public record RegisterCommand(
    String name,
    String email,
    String password,
    String confirmPassword
) {}
