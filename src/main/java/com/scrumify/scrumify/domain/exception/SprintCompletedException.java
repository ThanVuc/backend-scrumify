package com.scrumify.scrumify.domain.exception;

public class SprintCompletedException extends BusinessValidationException {
    public SprintCompletedException() {
        super("core.sprint.already_completed", "Completed sprint cannot be assigned");
    }
}
