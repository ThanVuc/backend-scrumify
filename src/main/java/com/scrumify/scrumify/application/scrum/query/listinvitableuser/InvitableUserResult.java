package com.scrumify.scrumify.application.scrum.query.listinvitableuser;

import java.util.UUID;

public record InvitableUserResult(
        UUID id,
        String fullName,
        String email) {
}
