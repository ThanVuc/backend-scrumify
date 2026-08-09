package com.scrumify.scrumify.domain.exception;

public class ConflictException extends BaseException {

    protected ConflictException(String code, String detail) {
        super(code, detail);
    }
}
