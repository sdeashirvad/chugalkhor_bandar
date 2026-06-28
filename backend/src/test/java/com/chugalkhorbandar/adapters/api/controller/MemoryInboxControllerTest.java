package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxGenerationSnapshot;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxGenerationTraceEntry;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxService;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxSource;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxStatus;
import com.chugalkhorbandar.application.session.SessionConstants;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MemoryInboxController.class)
@Import(ApiExceptionHandler.class)
class MemoryInboxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemoryInboxService memoryInboxService;

    @Test
    void listReturnsInboxItems() throws Exception {
        when(memoryInboxService.listForSession(eq("session-1"))).thenReturn(List.of(sampleItem(MemoryInboxStatus.NEW)));

        mockMvc.perform(get("/api/memory/inbox").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source").value("CONVERSATION_ARTIFACT"))
                .andExpect(jsonPath("$[0].status").value("NEW"));
    }

    @Test
    void getReturnsItemDetails() throws Exception {
        when(memoryInboxService.getForSession(eq("session-1"), eq("inbox-1")))
                .thenReturn(sampleItem(MemoryInboxStatus.NEW));

        mockMvc.perform(get("/api/memory/inbox/inbox-1").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("inbox-1"))
                .andExpect(jsonPath("$.importance").value("HIGH"));
    }

    @Test
    void reviewUpdatesStatus() throws Exception {
        when(memoryInboxService.review(eq("session-1"), eq("inbox-1")))
                .thenReturn(sampleItem(MemoryInboxStatus.REVIEWED));

        mockMvc.perform(post("/api/memory/inbox/inbox-1/review").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REVIEWED"));
    }

    @Test
    void discardArchivesItem() throws Exception {
        when(memoryInboxService.discard(eq("session-1"), eq("inbox-1")))
                .thenReturn(sampleItem(MemoryInboxStatus.ARCHIVED));

        mockMvc.perform(post("/api/memory/inbox/inbox-1/discard").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }

    @Test
    void devAllReturnsFullHistory() throws Exception {
        when(memoryInboxService.listAllForDeveloper(eq("session-1")))
                .thenReturn(List.of(sampleItem(MemoryInboxStatus.ARCHIVED)));

        mockMvc.perform(get("/api/memory/inbox/dev/all").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ARCHIVED"));
    }

    @Test
    void devGenerationReturnsTrace() throws Exception {
        when(memoryInboxService.getLatestGeneration(eq("session-1")))
                .thenReturn(Optional.of(new MemoryInboxGenerationSnapshot(
                        "character_alpha",
                        "conv-1",
                        Instant.parse("2026-06-01T12:00:00Z"),
                        List.of(new MemoryInboxGenerationTraceEntry("promise-artifact-rule", "PROMISE artifact detected")),
                        List.of(sampleItem(MemoryInboxStatus.NEW)))));

        mockMvc.perform(get("/api/memory/inbox/dev/generation").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value("conv-1"))
                .andExpect(jsonPath("$.trace[0].rule").value("promise-artifact-rule"));
    }

    private static MemoryInboxItem sampleItem(MemoryInboxStatus status) {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        return new MemoryInboxItem(
                "inbox-1",
                "PROMISE",
                MemoryInboxSource.CONVERSATION_ARTIFACT,
                "art-1",
                "character_alpha",
                "Remember this promise",
                MemoryInboxImportance.HIGH,
                0.85,
                status,
                now,
                now.plusSeconds(86400 * 30),
                Map.of("evidence", "Remember this promise"),
                List.of("created:promise-artifact"),
                "",
                List.of("art-1"));
    }
}
