package com.scrumify.scrumify.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scrumify.scrumify.api.error.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String traceId = MDC.get("traceId");
        ApiErrorResponse body = new ApiErrorResponse("auth.token.invalid", "Access token is invalid or expired", request.getRequestURI(), traceId);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        byte[] json = mapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        response.getOutputStream().write(json);
    }
}
