package com.scrumify.scrumify.application.scrum.command.invitemember;

import java.util.List;
import java.util.UUID;

public record InviteMemberCommand(
        UUID projectId,
        List<String> emails) {
}
