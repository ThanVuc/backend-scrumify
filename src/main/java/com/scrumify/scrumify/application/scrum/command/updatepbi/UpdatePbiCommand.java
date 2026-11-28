package com.scrumify.scrumify.application.scrum.command.updatepbi;

import java.util.UUID;

import com.scrumify.scrumify.domain.entity.pbi.PbiPriority;
import com.scrumify.scrumify.domain.entity.pbi.PbiStatus;
import com.scrumify.scrumify.domain.entity.pbi.PbiType;

public record UpdatePbiCommand(
        UUID pbiId,
        boolean titlePresent,
        String title,
        boolean descriptionPresent,
        String description,
        boolean typePresent,
        PbiType type,
        boolean priorityPresent,
        PbiPriority priority,
        boolean storyPointsPresent,
        Integer storyPoints,
        boolean epicIdPresent,
        UUID epicId,
        boolean statusPresent,
        PbiStatus status,
        boolean sprintIdPresent,
        UUID sprintId,
        Boolean moveToTop,
        Boolean archived) {
}
