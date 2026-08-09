package com.scrumify.scrumify.domain.exception;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException() {
        super("core.user.not_found", "User not found");
    }
}
