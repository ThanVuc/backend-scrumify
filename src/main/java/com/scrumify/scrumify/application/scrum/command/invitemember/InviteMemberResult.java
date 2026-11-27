package com.scrumify.scrumify.application.scrum.command.invitemember;

import java.util.UUID;

public record InviteMemberResult(
        UUID id,
        UUID userId,
        String name,
        String role) {
}
