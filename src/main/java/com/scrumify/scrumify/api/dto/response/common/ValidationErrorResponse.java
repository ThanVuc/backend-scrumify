package com.scrumify.scrumify.api.dto.response.common;

import java.util.List;

public record ValidationErrorResponse(
        String code,
        List<FieldError> errors,
        String path,
        String traceId) {

    public record FieldError(
            String field,
            String message) {
    }
}
