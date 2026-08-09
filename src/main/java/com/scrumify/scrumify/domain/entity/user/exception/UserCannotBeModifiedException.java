package com.scrumify.scrumify.domain.entity.user.exception;

import com.scrumify.scrumify.domain.exception.BusinessValidationException;

public class UserCannotBeModifiedException extends BusinessValidationException {

    public UserCannotBeModifiedException() {
        super("core.user.cannot_be_modified", "User cannot be modified in its current status");
    }
}
