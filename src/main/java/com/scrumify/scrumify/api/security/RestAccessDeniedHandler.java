package com.scrumify.scrumify.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scrumify.scrumify.api.dto.response.common.ApiErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String traceId = MDC.get("traceId");
        ApiErrorResponse body = new ApiErrorResponse("core.user.insufficient_permission", "You do not have permission to perform this action", request.getRequestURI(), traceId);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        byte[] json = mapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        response.getOutputStream().write(json);
    }
}
