package com.scrumify.scrumify.application.auth.command.login;

public record LoginCommand(
    String email,
    String password
) {}