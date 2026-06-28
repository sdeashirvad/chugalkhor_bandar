package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.InvalidLoginException;
import com.chugalkhorbandar.application.session.SessionConstants;
import com.chugalkhorbandar.application.session.SessionService;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.config.ChugalkhorProperties;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SessionController.class)
@Import({ApiExceptionHandler.class, ChugalkhorProperties.class})
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    @Test
    void loginReturnsSession() throws Exception {
        when(sessionService.login("Alpha", "secret"))
                .thenReturn(new ChatSession(
                        "session-1",
                        new CurrentCharacter("character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                        Instant.parse("2026-06-27T12:00:00Z"),
                        Instant.parse("2026-06-27T12:00:00Z"),
                        SessionStatus.ACTIVE));

        mockMvc.perform(post("/api/session/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"animalName\":\"Alpha\",\"passkey\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-1"))
                .andExpect(jsonPath("$.currentCharacter.displayName").value("Alpha"));
    }

    @Test
    void loginFailureReturns401() throws Exception {
        when(sessionService.login(anyString(), anyString())).thenThrow(new InvalidLoginException());

        mockMvc.perform(post("/api/session/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"animalName\":\"Alpha\",\"passkey\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void currentSessionRequiresActiveSession() throws Exception {
        when(sessionService.currentSession("session-1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/session/current").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void currentSessionReturnsCharacter() throws Exception {
        when(sessionService.currentSession("session-1"))
                .thenReturn(Optional.of(new ChatSession(
                        "session-1",
                        new CurrentCharacter("character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                        Instant.parse("2026-06-27T12:00:00Z"),
                        Instant.parse("2026-06-27T12:00:00Z"),
                        SessionStatus.ACTIVE)));

        mockMvc.perform(get("/api/session/current").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentCharacter.species").value("Rabbitu"));
    }

    @Test
    void logoutReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/session/logout").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isNoContent());
    }
}
