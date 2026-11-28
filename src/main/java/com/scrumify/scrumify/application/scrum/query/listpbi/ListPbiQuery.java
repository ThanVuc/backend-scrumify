package com.scrumify.scrumify.application.scrum.query.listpbi;

import java.util.UUID;

import com.scrumify.scrumify.domain.entity.pbi.PbiStatus;
import com.scrumify.scrumify.domain.entity.pbi.PbiType;

public record ListPbiQuery(
        UUID projectId,
        PbiStatus status,
        PbiType type,
        UUID epicId,
        String search) {
}
