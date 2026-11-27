package com.scrumify.scrumify.api.mapper;

import java.util.List;
import java.util.UUID;

import com.scrumify.scrumify.api.dto.request.member.InviteMemberRequest;
import com.scrumify.scrumify.api.dto.response.member.InvitableUserListResponse;
import com.scrumify.scrumify.api.dto.response.member.InviteMembersResponse;
import com.scrumify.scrumify.api.dto.response.member.MemberResponse;
import com.scrumify.scrumify.application.scrum.command.invitemember.InviteMemberCommand;
import com.scrumify.scrumify.application.scrum.command.invitemember.InviteMemberResult;
import com.scrumify.scrumify.application.scrum.query.listinvitableuser.InvitableUserResult;
import com.scrumify.scrumify.application.scrum.query.listinvitableuser.ListInvitableUsersQuery;

public final class MemberMapper {
    private MemberMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static InviteMemberCommand toInviteMemberCommand(UUID projectId, InviteMemberRequest request) {
        return new InviteMemberCommand(projectId, request.members());
    }

    public static InviteMembersResponse toInviteMembersResponse(List<InviteMemberResult> results) {
        var items = results.stream()
                .map(result -> new MemberResponse(
                        result.id(),
                        result.userId(),
                        result.name(),
                        result.role()))
                .toList();
        return new InviteMembersResponse(items);
    }

    public static ListInvitableUsersQuery toListInvitableUsersQuery(UUID projectId, List<String> emails) {
        return new ListInvitableUsersQuery(projectId, emails);
    }

    public static InvitableUserListResponse toInvitableUserListResponse(List<InvitableUserResult> results) {
        var items = results.stream()
                .map(result -> new InvitableUserListResponse.InvitableUser(
                        result.id(),
                        result.fullName(),
                        result.email()))
                .toList();
        return new InvitableUserListResponse(items);
    }
}
