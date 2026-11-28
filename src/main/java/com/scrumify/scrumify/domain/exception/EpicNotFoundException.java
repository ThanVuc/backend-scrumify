package com.scrumify.scrumify.domain.exception;

public class EpicNotFoundException extends ResourceNotFoundException {
    public EpicNotFoundException() {
        super("core.epic.not_found", "Epic not found");
    }
}
