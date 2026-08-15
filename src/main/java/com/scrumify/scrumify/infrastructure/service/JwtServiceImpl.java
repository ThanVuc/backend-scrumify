package com.scrumify.scrumify.infrastructure.service;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.scrumify.scrumify.application.common.interfaces.JwtService;
import com.scrumify.scrumify.application.common.model.JwtClaim;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtServiceImpl(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public String generateAccessToken(JwtClaim claim) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(86400)) // 1 day expiration
                .subject(claim.id().toString())
                .claim("username", claim.username())
                .claim("roles", claim.roles())
                .claim("email", claim.email())
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    @Override
    public JwtClaim verifyAccessToken(String token) {
        var jwt = jwtDecoder.decode(token);
        var id = java.util.UUID.fromString(jwt.getSubject());
        var username = jwt.getClaimAsString("username");
        var email = jwt.getClaimAsString("email");
        var role = jwt.getClaimAsString("roles");

        return new JwtClaim(id, username, email, role);
    }
}
