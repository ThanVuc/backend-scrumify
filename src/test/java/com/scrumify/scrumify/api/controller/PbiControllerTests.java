package com.scrumify.scrumify.api.controller;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.scrumify.scrumify.api.advice.GlobalExceptionHandler;
import com.scrumify.scrumify.application.scrum.command.createpbi.CreatePbiCommandHandler;
import com.scrumify.scrumify.application.scrum.command.updatepbi.UpdatePbiCommandHandler;
import com.scrumify.scrumify.application.scrum.query.detailpbi.DetailPbiQueryHandler;
import com.scrumify.scrumify.application.scrum.query.detailpbi.PbiDetailResult;
import com.scrumify.scrumify.application.scrum.query.listpbi.BacklogResult;
import com.scrumify.scrumify.application.scrum.query.listpbi.ListPbiQueryHandler;
import com.scrumify.scrumify.application.scrum.query.listpbi.PbiResult;

class PbiControllerTests {

    private ListPbiQueryHandler listPbiQueryHandler;
    private DetailPbiQueryHandler detailPbiQueryHandler;
    private CreatePbiCommandHandler createPbiCommandHandler;
    private UpdatePbiCommandHandler updatePbiCommandHandler;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        listPbiQueryHandler = mock(ListPbiQueryHandler.class);
        detailPbiQueryHandler = mock(DetailPbiQueryHandler.class);
        createPbiCommandHandler = mock(CreatePbiCommandHandler.class);
        updatePbiCommandHandler = mock(UpdatePbiCommandHandler.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new PbiController(
                        listPbiQueryHandler,
                        detailPbiQueryHandler,
                        createPbiCommandHandler,
                        updatePbiCommandHandler))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void list_returns200WithBacklog() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID backlogId = UUID.randomUUID();
        when(listPbiQueryHandler.handle(any()))
                .thenReturn(new BacklogResult(backlogId, projectId, List.of()));

        mockMvc.perform(get("/api/v1/projects/{projectId}/pbis", projectId)
                        .param("status", "TODO")
                        .param("type", "feature"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(backlogId.toString()))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()))
                .andExpect(jsonPath("$.pbis").isArray());
    }

    @Test
    void list_withInvalidStatus_returns400ValidationError() throws Exception {
        mockMvc.perform(get("/api/v1/projects/{projectId}/pbis", UUID.randomUUID())
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void create_withValidBody_returns201() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID pbiId = UUID.randomUUID();
        UUID backlogId = UUID.randomUUID();
        Instant now = Instant.parse("2026-11-28T00:00:00Z");

        when(createPbiCommandHandler.handle(any())).thenReturn(new PbiResult(
                pbiId, "SCRUM-1", backlogId, null, null, null,
                "Add board", null, "feature", "TODO", "MEDIUM", 5, now, now, null));

        mockMvc.perform(post("/api/v1/projects/{projectId}/pbis", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Add board","type":"feature","storyPoints":5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(pbiId.toString()))
                .andExpect(jsonPath("$.code").value("SCRUM-1"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    void create_withInvalidBody_returns400ValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/projects/{projectId}/pbis", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","type":"unknown","storyPoints":-1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void detail_returns200() throws Exception {
        UUID pbiId = UUID.randomUUID();
        UUID backlogId = UUID.randomUUID();
        Instant now = Instant.parse("2026-11-28T00:00:00Z");

        PbiResult pbiResult = new PbiResult(
                pbiId, "SCRUM-2", backlogId, null, null, null,
                "Detail", null, "bug", "INPROGRESS", "HIGH", 3, now, now, null);
        when(detailPbiQueryHandler.handle(any())).thenReturn(new PbiDetailResult(pbiResult, List.of()));

        mockMvc.perform(get("/api/v1/pbis/{pbiId}", pbiId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pbi.id").value(pbiId.toString()))
                .andExpect(jsonPath("$.tasks").isArray());
    }

    @Test
    void update_tracksExplicitNullVsOmittedFields() throws Exception {
        UUID pbiId = UUID.randomUUID();
        UUID backlogId = UUID.randomUUID();
        Instant now = Instant.parse("2026-11-28T00:00:00Z");

        when(updatePbiCommandHandler.handle(any())).thenReturn(new PbiResult(
                pbiId, "SCRUM-2", backlogId, null, null, null,
                "Detail", null, "bug", "DONE", "HIGH", 3, now, now, null));

        mockMvc.perform(patch("/api/v1/pbis/{pbiId}", pbiId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"epicId":null,"status":"DONE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));

        verify(updatePbiCommandHandler).handle(argThat(cmd ->
                cmd.epicIdPresent()
                        && cmd.epicId() == null
                        && cmd.statusPresent()
                        && !cmd.sprintIdPresent()
                        && !cmd.titlePresent()));
    }
}
