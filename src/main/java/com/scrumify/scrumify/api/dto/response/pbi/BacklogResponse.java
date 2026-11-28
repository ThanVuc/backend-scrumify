package com.scrumify.scrumify.api.dto.response.pbi;

import java.util.List;
import java.util.UUID;

public record BacklogResponse(
        UUID id,
        UUID projectId,
        List<PbiResponse> pbis) {
}
