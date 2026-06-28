package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.behavior.BehaviorPlanningTrace;
import com.chugalkhorbandar.application.behavior.BehaviorPlanningTraceEntry;
import com.chugalkhorbandar.application.behavior.BehaviorProfile;
import com.chugalkhorbandar.application.behavior.BehaviorProfileSnapshot;
import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.application.behavior.ConversationFlavor;
import com.chugalkhorbandar.application.behavior.CuriosityLevel;
import com.chugalkhorbandar.application.behavior.EnergyModifier;
import com.chugalkhorbandar.application.behavior.EndingStyle;
import com.chugalkhorbandar.application.behavior.HumorLevel;
import com.chugalkhorbandar.application.behavior.NarrationStyle;
import com.chugalkhorbandar.application.behavior.OpeningStyle;
import com.chugalkhorbandar.application.behavior.StorytellingPreference;
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

@WebMvcTest(controllers = BehaviorController.class)
@Import(ApiExceptionHandler.class)
class BehaviorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BehaviorEngineService behaviorEngineService;

    @Test
    void currentProfileReturnsLatestSnapshot() throws Exception {
        when(behaviorEngineService.getCurrentProfile(eq("session-1")))
                .thenReturn(Optional.of(new BehaviorProfileSnapshot(
                        "session-1",
                        new BehaviorProfile(
                                OpeningStyle.OBSERVATION,
                                NarrationStyle.STORY,
                                HumorLevel.LIGHT,
                                CuriosityLevel.HIGH,
                                EndingStyle.QUESTION,
                                ConversationFlavor.NOSTALGIC,
                                EnergyModifier.LIVELY,
                                StorytellingPreference.STRONG,
                                Instant.parse("2026-01-01T00:00:00Z")),
                        new BehaviorPlanningTrace(
                                List.of(new BehaviorPlanningTraceEntry("story-behavior", "Conversation goal is storytelling"))))));

        mockMvc.perform(get("/api/behavior/current").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationFlavor").value("NOSTALGIC"))
                .andExpect(jsonPath("$.narrationStyle").value("STORY"))
                .andExpect(jsonPath("$.humorLevel").value("LIGHT"))
                .andExpect(jsonPath("$.trace[0].rule").value("story-behavior"));
    }
}
