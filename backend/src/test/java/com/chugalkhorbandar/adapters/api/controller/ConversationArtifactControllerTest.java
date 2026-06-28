package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactEngine;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactPriority;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactService;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.session.SessionConstants;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ConversationArtifactController.class)
@Import(ApiExceptionHandler.class)
class ConversationArtifactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationArtifactService artifactService;

    @Test
    void listReturnsArtifacts() throws Exception {
        when(artifactService.listForSession(eq("session-1")))
                .thenReturn(List.of(new ConversationArtifact(
                        "a-1",
                        ConversationArtifactType.PROMISE,
                        ConversationArtifactEngine.BANDAR_CHARACTER_ID,
                        "character_alpha",
                        "character_alpha",
                        "conv-1",
                        "Promise",
                        "Bandar made a promise during conversation.",
                        ConversationArtifactStatus.ACTIVE,
                        ConversationArtifactPriority.HIGH,
                        Instant.parse("2026-01-01T00:00:00Z"),
                        Instant.parse("2026-01-01T00:00:00Z"),
                        Instant.parse("2026-02-01T00:00:00Z"),
                        Map.of("trigger", "promise-made"),
                        List.of("created:promise-made", "activated"))));

        mockMvc.perform(get("/api/artifacts").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("PROMISE"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void fulfillUpdatesArtifact() throws Exception {
        when(artifactService.fulfill(eq("session-1"), eq("a-1")))
                .thenReturn(new ConversationArtifact(
                        "a-1",
                        ConversationArtifactType.PROMISE,
                        ConversationArtifactEngine.BANDAR_CHARACTER_ID,
                        "character_alpha",
                        "character_alpha",
                        "conv-1",
                        "Promise",
                        "summary",
                        ConversationArtifactStatus.FULFILLED,
                        ConversationArtifactPriority.HIGH,
                        Instant.parse("2026-01-01T00:00:00Z"),
                        Instant.parse("2026-01-02T00:00:00Z"),
                        Instant.parse("2026-02-01T00:00:00Z"),
                        Map.of(),
                        List.of("fulfilled")));

        mockMvc.perform(post("/api/artifacts/a-1/fulfill").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FULFILLED"));
    }
}
