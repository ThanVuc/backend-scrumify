package com.scrumify.scrumify.application.scrum.query.listinvitableuser;

import java.util.List;
import java.util.UUID;

public record ListInvitableUsersQuery(
        UUID projectId,
        List<String> emails) {
}
