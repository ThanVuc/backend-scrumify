package com.scrumify.scrumify.api.dto.response.common;

public record ApiErrorResponse(
        String code,
        String detail,
        String path,
        String traceId
) {}
