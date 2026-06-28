package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryFieldTrace;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.memory.working.WorkingMemorySnapshot;
import com.chugalkhorbandar.application.session.SessionConstants;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WorkingMemoryController.class)
@Import(ApiExceptionHandler.class)
class WorkingMemoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkingMemoryService workingMemoryService;

    @Test
    void getReturnsWorkingMemory() throws Exception {
        when(workingMemoryService.getOrBuild(eq("session-1"))).thenReturn(snapshot("session-1", 1L));

        mockMvc.perform(get("/api/memory/working").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-1"))
                .andExpect(jsonPath("$.activeTopic").value("Location"))
                .andExpect(jsonPath("$.fieldTraces[0].field").value("activeTopic"));
    }

    @Test
    void rebuildReturnsFreshSnapshot() throws Exception {
        when(workingMemoryService.rebuild(eq("session-1"))).thenReturn(snapshot("session-1", 2L));

        mockMvc.perform(post("/api/memory/working/rebuild").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(2));

        verify(workingMemoryService).rebuild("session-1");
    }

    private static WorkingMemorySnapshot snapshot(String sessionId, long version) {
        WorkingMemory memory = new WorkingMemory(
                sessionId,
                "Location",
                "Curious",
                "",
                List.of("Hippu King"),
                List.of("Where am I?"),
                List.of(),
                List.of("The speaker may be asking about Hippu Palace."),
                Instant.parse("2026-01-01T00:00:00Z"),
                version);
        return new WorkingMemorySnapshot(
                memory, List.of(new WorkingMemoryFieldTrace("activeTopic", "Location", "Location signal")));
    }
}
