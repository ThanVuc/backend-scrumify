package com.scrumify.scrumify.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scrumify.scrumify.api.dto.request.auth.LoginRequest;
import com.scrumify.scrumify.api.dto.request.auth.RegisterRequest;
import com.scrumify.scrumify.api.dto.response.auth.MeResponse;
import com.scrumify.scrumify.api.mapper.AuthMapper;
import com.scrumify.scrumify.application.auth.command.login.LoginCommandHandler;
import com.scrumify.scrumify.application.auth.command.register.RegisterCommandHandler;
import com.scrumify.scrumify.application.auth.query.me.MeQueryHandler;

import ch.qos.logback.core.util.Duration;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

@RestController
@RequestMapping("/api/v1/as")
@Slf4j
public class AuthController {
    private final RegisterCommandHandler registerCommandHandler;
    private final LoginCommandHandler loginCommandHandler;
    private final MeQueryHandler meQueryHandler;

    public AuthController(
            RegisterCommandHandler registerCommandHandler,
            LoginCommandHandler loginCommandHandler,
            MeQueryHandler meQueryHandler) {
        this.registerCommandHandler = registerCommandHandler;
        this.loginCommandHandler = loginCommandHandler;
        this.meQueryHandler = meQueryHandler;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        var command = AuthMapper.toRegisterCommand(request);
        var accessToken = registerCommandHandler.Handle(command);

        var cookie = ResponseCookie.from("scrumify_access_token", accessToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString());
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        var command = AuthMapper.toLoginCommand(request);
        var accessToken = loginCommandHandler.Handle(command);

        var cookie = ResponseCookie.from("scrumify_access_token", accessToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MEMBER')")
    public MeResponse me() {
        var result = meQueryHandler.Handle();
        return AuthMapper.toMeResponse(result);
    }

        @PostMapping("/logout")
        @ResponseStatus(HttpStatus.OK)
        @PreAuthorize("hasRole('MEMBER')")
        public void logout(HttpServletResponse response) {
                var cookie = ResponseCookie.from("scrumify_access_token", "")
                                .httpOnly(true)
                                .secure(false)
                                .sameSite("Lax")
                                .path("/")
                                .maxAge(0)
                                .build();

                response.addHeader(
                                HttpHeaders.SET_COOKIE,
                                cookie.toString());
        }
}
