package com.scrumify.scrumify.domain.entity.user.exception;

import com.scrumify.scrumify.domain.exception.BusinessValidationException;

public class PasswordAndConfirmNotMatch extends BusinessValidationException {
    public PasswordAndConfirmNotMatch() {
        super("core.user.password_and_confirm_not_match", "Password and confirm password do not match");
    }
}
