package com.scrumify.scrumify.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.scrumify.scrumify.api.advice.GlobalExceptionHandler;
import com.scrumify.scrumify.application.scrum.command.invitemember.InviteMemberCommandHandler;
import com.scrumify.scrumify.application.scrum.command.invitemember.InviteMemberResult;
import com.scrumify.scrumify.application.scrum.query.listinvitableuser.InvitableUserResult;
import com.scrumify.scrumify.application.scrum.query.listinvitableuser.ListInvitableUsersQueryHandler;

class MemberControllerTests {

    private InviteMemberCommandHandler inviteMemberCommandHandler;
    private ListInvitableUsersQueryHandler listInvitableUsersQueryHandler;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        inviteMemberCommandHandler = mock(InviteMemberCommandHandler.class);
        listInvitableUsersQueryHandler = mock(ListInvitableUsersQueryHandler.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new MemberController(inviteMemberCommandHandler, listInvitableUsersQueryHandler))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void invite_withValidRequest_returns201WithItems() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(inviteMemberCommandHandler.handle(any()))
                .thenReturn(List.of(new InviteMemberResult(memberId, userId, "Alex", "member")));

        mockMvc.perform(post("/api/v1/projects/{projectId}/members", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"members":["alex@example.com"]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items[0].id").value(memberId.toString()))
                .andExpect(jsonPath("$.items[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$.items[0].name").value("Alex"))
                .andExpect(jsonPath("$.items[0].role").value("member"));
    }

    @Test
    void invite_withEmptyMembersOrInvalidEmail_returns400ValidationError() throws Exception {
        UUID projectId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/projects/{projectId}/members", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"members":[]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isNotEmpty());

        mockMvc.perform(post("/api/v1/projects/{projectId}/members", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"members":["not-an-email"]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void invite_withInvalidProjectUuid_returns400ValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/projects/not-a-uuid/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"members":["alex@example.com"]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors[0].field").value("projectId"));
    }

    @Test
    void listInvitableUsers_withAndWithoutEmails_returns200() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(listInvitableUsersQueryHandler.handle(any()))
                .thenReturn(List.of())
                .thenReturn(List.of(new InvitableUserResult(userId, "Alex", "alex@example.com")));

        mockMvc.perform(get("/api/v1/projects/{projectId}/invitable-users", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());

        mockMvc.perform(get("/api/v1/projects/{projectId}/invitable-users", projectId)
                        .param("emails", "alex@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(userId.toString()))
                .andExpect(jsonPath("$.items[0].fullName").value("Alex"))
                .andExpect(jsonPath("$.items[0].email").value("alex@example.com"));
    }

    @Test
    void listInvitableUsers_withInvalidEmailParam_returns400ValidationError() throws Exception {
        UUID projectId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/projects/{projectId}/invitable-users", projectId)
                        .param("emails", "invalid-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
