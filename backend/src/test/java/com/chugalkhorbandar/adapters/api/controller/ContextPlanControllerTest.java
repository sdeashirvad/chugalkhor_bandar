package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.context.ContextPlan;
import com.chugalkhorbandar.application.context.ContextPlanService;
import com.chugalkhorbandar.application.context.ContextPlanningTrace;
import com.chugalkhorbandar.application.context.ContextPlanningTraceEntry;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextResolveService;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlan;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlanningTrace;
import com.chugalkhorbandar.application.session.SessionConstants;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ContextPlanController.class)
@Import(ApiExceptionHandler.class)
class ContextPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContextPlanService contextPlanService;

    @MockitoBean
    private ContextResolveService contextResolveService;

    @Test
    void planReturnsContextSectionsAndTrace() throws Exception {
        when(contextPlanService.plan(eq("session-1"), eq("Where am I?")))
                .thenReturn(new ContextPlan(
                        List.of(new ContextSection(
                                ContextSectionType.CURRENT_LOCATION,
                                60,
                                "places",
                                new ContextReference("places", "place", "place_1", "details", 60),
                                4)),
                        new KnowledgeFragmentPlan(List.of(), 4, new KnowledgeFragmentPlanningTrace(List.of())),
                        4,
                        new ContextPlanningTrace(List.of(new ContextPlanningTraceEntry(
                                ContextSectionType.CURRENT_LOCATION, "CHARACTER_LOCATION — User asked about location (\"where\")")))));

        mockMvc.perform(post("/api/context/plan")
                        .header(SessionConstants.SESSION_HEADER, "session-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latestMessage\":\"Where am I?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections[0].type").value("CURRENT_LOCATION"))
                .andExpect(jsonPath("$.trace.entries[0].reason")
                        .value("CHARACTER_LOCATION — User asked about location (\"where\")"));
    }
}
