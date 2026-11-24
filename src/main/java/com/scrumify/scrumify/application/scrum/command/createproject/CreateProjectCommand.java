package com.scrumify.scrumify.application.scrum.command.createproject;

public record CreateProjectCommand(
        String name,
        String description,
        String codePrefix) {
}
