package com.scrumify.scrumify.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scrumify.scrumify.api.dto.request.RegisterRequest;
import com.scrumify.scrumify.api.mapper.AuthMapper;
import com.scrumify.scrumify.application.auth.command.register.RegisterCommandHandler;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final RegisterCommandHandler registerCommandHandler;

    public AuthController(RegisterCommandHandler registerCommandHandler) {
        this.registerCommandHandler = registerCommandHandler;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
        @Valid @RequestBody RegisterRequest request
    ) {
        var command = AuthMapper.toRegisterCommand(request);
        registerCommandHandler.Handle(command);
    }
}
