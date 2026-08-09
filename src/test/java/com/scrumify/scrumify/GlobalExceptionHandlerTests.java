package com.scrumify.scrumify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void resourceNotFoundReturns404() throws Exception {
        mockMvc.perform(get("/api/test/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("core.user.not_found"))
                .andExpect(header().exists("X-Trace-Id"))
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void conflictReturns409() throws Exception {
        mockMvc.perform(get("/api/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("core.user.email_already_exists"))
                .andExpect(header().exists("X-Trace-Id"));
    }

    @Test
    void businessValidationReturns422() throws Exception {
        mockMvc.perform(get("/api/test/business"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("core.order.cannot_be_modified"))
                .andExpect(header().exists("X-Trace-Id"));
    }

    @Test
    void unexpectedReturns500() throws Exception {
        mockMvc.perform(get("/api/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("internal.server_error"))
                .andExpect(header().exists("X-Trace-Id"));
    }

    @Test
    void unauthenticatedReturns401() throws Exception {
        mockMvc.perform(get("/api/test/secure"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("auth.token.invalid"))
                .andExpect(header().exists("X-Trace-Id"));
    }

    @Test
    void unauthorizedReturns403() throws Exception {
        mockMvc.perform(get("/api/test/admin").with(user("user").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("core.user.insufficient_permission"))
                .andExpect(header().exists("X-Trace-Id"));
    }
}
