package com.scrumify.scrumify.api.dto.response.member;

import java.util.List;
import java.util.UUID;

public record InvitableUserListResponse(
        List<InvitableUser> items) {

    public record InvitableUser(
            UUID id,
            String fullName,
            String email) {
    }
}
