package com.scrumify.scrumify.application.common.model;

import java.util.Objects;
import java.util.UUID;

public record JwtClaim(
    UUID id,
    String username,
    String email,
    String roles
) {
    public JwtClaim {
        Objects.requireNonNull(id);
        Objects.requireNonNull(username);
        Objects.requireNonNull(email);
        Objects.requireNonNull(roles);
    }
}