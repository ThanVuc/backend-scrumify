package com.scrumify.scrumify.domain.exception;

public class BusinessValidationException extends BaseException {

    protected BusinessValidationException(String code, String detail) {
        super(code, detail);
    }
}
