package com.scrumify.scrumify.application.scrum.command.createpbi;

import java.util.UUID;

import com.scrumify.scrumify.domain.entity.pbi.PbiPriority;
import com.scrumify.scrumify.domain.entity.pbi.PbiType;

public record CreatePbiCommand(
        UUID projectId,
        String title,
        String description,
        PbiType type,
        PbiPriority priority,
        Integer storyPoints,
        UUID epicId,
        UUID sprintId) {
}
