package com.scrumify.scrumify.application.scrum.query.listpbi;

import java.util.List;
import java.util.UUID;

public record BacklogResult(
        UUID id,
        UUID projectId,
        List<PbiResult> pbis) {
}
