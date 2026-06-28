package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.LoginRequestDto;
import com.chugalkhorbandar.adapters.api.dto.SessionResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.SessionDtoMapper;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.SessionConstants;
import com.chugalkhorbandar.application.session.SessionService;
import com.chugalkhorbandar.application.session.UnauthorizedSessionException;
import com.chugalkhorbandar.config.ChugalkhorProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
@Tag(name = "Session", description = "World identity and session")
public class SessionController {

    private final SessionService sessionService;
    private final ChugalkhorProperties properties;

    public SessionController(SessionService sessionService, ChugalkhorProperties properties) {
        this.sessionService = sessionService;
        this.properties = properties;
    }

    @PostMapping("/login")
    @Operation(summary = "Login as a Jungle character")
    public ResponseEntity<SessionResponseDto> login(
            @RequestBody LoginRequestDto request, HttpServletResponse response) {
        ChatSession session = sessionService.login(request.animalName(), request.passkey());
        writeSessionCookie(response, session.sessionId());
        return ResponseEntity.ok(SessionDtoMapper.toDto(session));
    }

    @PostMapping("/logout")
    @Operation(summary = "End the current session")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        sessionService.logout(SessionRequestSupport.resolveSessionId(request));
        clearSessionCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current")
    @Operation(summary = "Get the current session and character identity")
    public SessionResponseDto current(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        ChatSession session = sessionService
                .currentSession(sessionId)
                .orElseThrow(UnauthorizedSessionException::new);
        return SessionDtoMapper.toDto(session);
    }

    private void writeSessionCookie(HttpServletResponse response, String sessionId) {
        ResponseCookie cookie = ResponseCookie.from(SessionConstants.SESSION_COOKIE, sessionId)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMinutes(properties.getSession().getInactivityMinutes()))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearSessionCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(SessionConstants.SESSION_COOKIE, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
