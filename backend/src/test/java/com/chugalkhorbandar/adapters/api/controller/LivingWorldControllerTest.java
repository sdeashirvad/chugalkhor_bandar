package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.world.living.LivingWorldProperties;
import com.chugalkhorbandar.application.world.living.LivingWorldService;
import com.chugalkhorbandar.application.world.living.LivingWorldTickResult;
import com.chugalkhorbandar.application.world.living.WorldClockMode;
import com.chugalkhorbandar.application.world.living.WorldEvent;
import com.chugalkhorbandar.application.world.living.WorldEventOrigin;
import com.chugalkhorbandar.application.world.living.WorldEventStatus;
import com.chugalkhorbandar.application.world.living.WorldEventType;
import com.chugalkhorbandar.application.world.living.WorldEventVisibility;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = LivingWorldController.class)
@Import(ApiExceptionHandler.class)
class LivingWorldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LivingWorldService livingWorldService;

    @MockitoBean
    private LivingWorldProperties properties;

    @Test
    void listEventsReturnsEvents() throws Exception {
        when(livingWorldService.listEvents()).thenReturn(List.of(sampleEvent()));

        mockMvc.perform(get("/api/world/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("FESTIVAL"));
    }

    @Test
    void runTickEndpointWorks() throws Exception {
        when(properties.isManualTickEnabled()).thenReturn(true);
        when(livingWorldService.runManualTick()).thenReturn(sampleTick());

        mockMvc.perform(post("/api/world/dev/run-tick"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.eventsGenerated").value(1));
    }

    private static WorldEvent sampleEvent() {
        Instant now = Instant.parse("2026-06-01T06:00:00Z");
        return new WorldEvent(
                "evt-festival-2026-06-01-test",
                WorldEventType.FESTIVAL,
                "Spring Festival",
                "Celebration today",
                List.of(),
                WorldEventVisibility.PUBLIC,
                now,
                LocalDate.parse("2026-06-01"),
                java.util.Map.of(),
                WorldEventStatus.ACTIVE,
                WorldEventOrigin.FESTIVAL_ENGINE);
    }

    private static LivingWorldTickResult sampleTick() {
        Instant now = Instant.parse("2026-06-01T06:00:00Z");
        return new LivingWorldTickResult(
                "tick-1",
                WorldClockMode.MANUAL,
                now,
                now,
                5,
                LocalDate.parse("2026-06-01"),
                1,
                1,
                1,
                List.of(sampleEvent()),
                List.of(),
                List.of("art-1"),
                List.of("notif-1"),
                List.of());
    }
}
