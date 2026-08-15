package com.scrumify.scrumify.infrastructure.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.common.model.JwtClaim;

@Service
@RequestScope
public class CurrentUserImpl implements CurrentUserService{
    private JwtClaim claim;

    @Override
    public void setCurrentUser(JwtClaim claim) {
        this.claim = claim;
    }

    @Override
    public JwtClaim getCurrentUser() {
        return claim;
    }
}
