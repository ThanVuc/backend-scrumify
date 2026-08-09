package com.scrumify.scrumify.domain.entity.user.exception;

import com.scrumify.scrumify.domain.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException() {
        super("core.user.not_found", "User not found");
    }
}
