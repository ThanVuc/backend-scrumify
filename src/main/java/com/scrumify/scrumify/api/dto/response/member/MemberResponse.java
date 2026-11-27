package com.scrumify.scrumify.api.dto.response.member;

import java.util.UUID;

public record MemberResponse(
        UUID id,
        UUID userId,
        String name,
        String role) {
}
