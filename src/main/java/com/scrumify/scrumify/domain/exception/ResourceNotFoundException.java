package com.scrumify.scrumify.domain.exception;

public class ResourceNotFoundException extends BaseException {

    protected ResourceNotFoundException(String code, String detail) {
        super(code, detail);
    }
}
