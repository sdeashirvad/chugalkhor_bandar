package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisService;
import com.chugalkhorbandar.application.cognition.Observation;
import com.chugalkhorbandar.application.cognition.ObservationType;
import com.chugalkhorbandar.application.cognition.Recommendation;
import com.chugalkhorbandar.application.cognition.RecommendationAction;
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

@WebMvcTest(controllers = CognitiveAnalysisController.class)
@Import(ApiExceptionHandler.class)
class CognitiveAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CognitiveAnalysisService cognitiveAnalysisService;

    @Test
    void latestReturnsAnalysis() throws Exception {
        when(cognitiveAnalysisService.getLatestForSession(eq("session-1")))
                .thenReturn(Optional.of(new CognitiveAnalysisResult(
                        "analysis-1",
                        "character_alpha",
                        "conv-1",
                        "mock",
                        "mock-cognitive-analysis",
                        12,
                        0.91,
                        Instant.parse("2026-01-01T00:00:00Z"),
                        List.of(new Observation(
                                "o-1",
                                ObservationType.PREFERENCE,
                                0.94,
                                "summary",
                                "evidence",
                                Map.of(),
                                Instant.parse("2026-01-01T00:00:00Z"))),
                        List.of(new Recommendation(
                                "r-1",
                                RecommendationAction.WAIT,
                                0.91,
                                "reason",
                                "conv-1",
                                Map.of())),
                        "{\"observations\":[]}")));

        mockMvc.perform(get("/api/cognition/latest").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("mock"))
                .andExpect(jsonPath("$.observations[0].type").value("PREFERENCE"));
    }
}
