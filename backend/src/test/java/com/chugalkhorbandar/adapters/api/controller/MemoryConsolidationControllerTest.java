package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationAsyncRunner;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationProperties;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MemoryConsolidationController.class)
@Import(ApiExceptionHandler.class)
class MemoryConsolidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemoryConsolidationService consolidationService;

    @MockitoBean
    private MemoryConsolidationAsyncRunner asyncRunner;

    @MockitoBean
    private MemoryConsolidationProperties properties;

    @Test
    void latestReturnsReport() throws Exception {
        when(consolidationService.getLatestReport()).thenReturn(Optional.of(sampleReport()));

        mockMvc.perform(get("/api/memory/consolidation/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.runId").value("run-1"))
                .andExpect(jsonPath("$.promoted").value(2));
    }

    @Test
    void historyReturnsReports() throws Exception {
        when(consolidationService.getHistory()).thenReturn(List.of(sampleReport()));

        mockMvc.perform(get("/api/memory/consolidation/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].candidateCount").value(1));
    }

    @Test
    void manualRunExecutesConsolidation() throws Exception {
        when(properties.isDeveloperManualRun()).thenReturn(true);
        when(consolidationService.runConsolidation()).thenReturn(sampleReport());

        mockMvc.perform(post("/api/memory/consolidation/run"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.summary").isNotEmpty());
    }

    private static MemoryConsolidationReport sampleReport() {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        return new MemoryConsolidationReport(
                "run-1",
                now,
                now,
                100,
                3,
                2,
                1,
                0,
                3,
                1,
                1,
                "Consolidation complete",
                "Jungle Daily Report",
                "{}",
                "Bandar slept.",
                "SKIPPED",
                "");
    }
}
