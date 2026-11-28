package com.scrumify.scrumify.domain.exception;

public class BacklogNotFoundException extends ResourceNotFoundException {
    public BacklogNotFoundException() {
        super("core.backlog.not_found", "Backlog not found");
    }
}
