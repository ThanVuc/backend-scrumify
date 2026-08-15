package com.scrumify.scrumify.application.common.interfaces;

import com.scrumify.scrumify.application.common.model.JwtClaim;

public interface CurrentUserService {
    public void setCurrentUser(JwtClaim claim);
    public JwtClaim getCurrentUser();
}
