package com.scrumify.scrumify.domain.exception;

public class UserCannotBeModifiedException extends BusinessValidationException {

    public UserCannotBeModifiedException() {
        super("core.user.cannot_be_modified", "User cannot be modified in its current status");
    }
}
