package com.scrumify.scrumify.domain.exception;

public class SprintNotFoundException extends ResourceNotFoundException {
    public SprintNotFoundException() {
        super("core.sprint.not_found", "Sprint not found");
    }
}
