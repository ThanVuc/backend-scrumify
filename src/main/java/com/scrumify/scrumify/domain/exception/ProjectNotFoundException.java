package com.scrumify.scrumify.domain.exception;

public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException() {
        super("core.project.not_found", "Project not found");
    }
}
