package com.scrumify.scrumify.application.common.interfaces;

import com.scrumify.scrumify.application.common.model.JwtClaim;

public interface JwtService {
    String generateAccessToken(JwtClaim claim);
    JwtClaim verifyAccessToken(String token);
}
