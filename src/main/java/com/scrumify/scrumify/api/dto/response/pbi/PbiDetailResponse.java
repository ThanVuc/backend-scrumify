package com.scrumify.scrumify.api.dto.response.pbi;

import java.util.List;

public record PbiDetailResponse(
        PbiResponse pbi,
        List<TaskResponse> tasks) {
}
