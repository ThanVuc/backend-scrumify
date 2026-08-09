package com.scrumify.scrumify.domain.exception;

public abstract class BaseException extends RuntimeException {

    private final String code;

    protected BaseException(String code, String detail) {
        super(detail);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getDetail() {
        return getMessage();
    }
}
