package com.scrumify.scrumify.api.error;

public record ApiErrorResponse(
        String code,
        String detail,
        String path,
        String traceId
) {}
