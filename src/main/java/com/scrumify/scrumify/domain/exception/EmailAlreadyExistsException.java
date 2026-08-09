package com.scrumify.scrumify.domain.exception;

public class EmailAlreadyExistsException extends ConflictException {

    public EmailAlreadyExistsException() {
        super("core.user.email_already_exists", "Email already exists");
    }
}
