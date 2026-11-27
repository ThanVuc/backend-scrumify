package com.scrumify.scrumify.api.dto.response.member;

import java.util.List;

public record InviteMembersResponse(
        List<MemberResponse> items) {
}
