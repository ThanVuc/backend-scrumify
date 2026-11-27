package com.scrumify.scrumify.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scrumify.scrumify.api.dto.request.member.InviteMemberRequest;
import com.scrumify.scrumify.api.dto.response.member.InvitableUserListResponse;
import com.scrumify.scrumify.api.dto.response.member.InviteMembersResponse;
import com.scrumify.scrumify.api.mapper.MemberMapper;
import com.scrumify.scrumify.application.scrum.command.invitemember.InviteMemberCommandHandler;
import com.scrumify.scrumify.application.scrum.query.listinvitableuser.ListInvitableUsersQueryHandler;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/v1/projects/{projectId}")
public class MemberController {
    private final InviteMemberCommandHandler inviteMemberCommandHandler;
    private final ListInvitableUsersQueryHandler listInvitableUsersQueryHandler;

    public MemberController(
            InviteMemberCommandHandler inviteMemberCommandHandler,
            ListInvitableUsersQueryHandler listInvitableUsersQueryHandler) {
        this.inviteMemberCommandHandler = inviteMemberCommandHandler;
        this.listInvitableUsersQueryHandler = listInvitableUsersQueryHandler;
    }

    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MEMBER')")
    public InviteMembersResponse invite(
            @PathVariable UUID projectId,
            @Valid @RequestBody InviteMemberRequest request) {
        var members = inviteMemberCommandHandler.handle(
                MemberMapper.toInviteMemberCommand(projectId, request));
        return MemberMapper.toInviteMembersResponse(members);
    }

    @GetMapping("/invitable-users")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MEMBER')")
    public InvitableUserListResponse listInvitableUsers(
            @PathVariable UUID projectId,
            @RequestParam(required = false) List<
                    @NotBlank(message = "Email is required")
                    @Email(message = "Email format is invalid")
                    @Size(max = 256, message = "Email must not exceed 256 characters")
                    String> emails) {
        var users = listInvitableUsersQueryHandler.handle(
                MemberMapper.toListInvitableUsersQuery(projectId, emails));
        return MemberMapper.toInvitableUserListResponse(users);
    }
}
