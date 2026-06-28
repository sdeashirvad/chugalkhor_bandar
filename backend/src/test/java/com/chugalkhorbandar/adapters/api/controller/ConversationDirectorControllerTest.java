package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.conversation.director.ConversationArc;
import com.chugalkhorbandar.application.conversation.director.ConversationEnergy;
import com.chugalkhorbandar.application.conversation.director.ConversationGoal;
import com.chugalkhorbandar.application.conversation.director.ConversationOutcome;
import com.chugalkhorbandar.application.conversation.director.ConversationPlan;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanningTrace;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanningTraceEntry;
import com.chugalkhorbandar.application.conversation.director.ConversationDirectorService;
import com.chugalkhorbandar.application.session.SessionConstants;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ConversationDirectorController.class)
@Import(ApiExceptionHandler.class)
class ConversationDirectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationDirectorService conversationDirectorService;

    @Test
    void currentPlanReturnsLatestSnapshot() throws Exception {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        ConversationPlan plan = new ConversationPlan(
                ConversationGoal.STORY,
                0.94,
                true,
                ConversationEnergy.HIGH,
                ConversationArc.QUESTION_STORY,
                2,
                List.of(500L),
                false,
                true,
                false,
                false,
                false,
                "Narrative",
                ConversationOutcome.STORY_STARTED,
                createdAt,
                false,
                false,
                Instant.parse("2026-01-01T00:00:01Z"),
                Instant.parse("2026-01-01T00:00:05Z"));
        when(conversationDirectorService.getCurrentPlan(eq("session-1")))
                .thenReturn(Optional.of(new ConversationPlanSnapshot(
                        "session-1",
                        plan,
                        new ConversationPlanningTrace(
                                List.of(new ConversationPlanningTraceEntry("story-request", "User requested a story"))),
                        true,
                        2,
                        0,
                        "",
                        List.of(),
                        List.of("msg-1", "msg-2"),
                        List.of())));

        mockMvc.perform(get("/api/conversation/director/current-plan")
                        .header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goal").value("STORY"))
                .andExpect(jsonPath("$.conversationEnergy").value("HIGH"))
                .andExpect(jsonPath("$.conversationArc").value("QUESTION_STORY"))
                .andExpect(jsonPath("$.expectedMessageCount").value(2))
                .andExpect(jsonPath("$.executed").value(true))
                .andExpect(jsonPath("$.executedMessageCount").value(2))
                .andExpect(jsonPath("$.trace[0].rule").value("story-request"));
    }
}
