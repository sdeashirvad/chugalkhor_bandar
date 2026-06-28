package com.chugalkhorbandar.application.session;

import java.time.Instant;

public record ChatSession(
        String sessionId,
        CurrentCharacter currentCharacter,
        Instant startedAt,
        Instant lastActivity,
        SessionStatus status) {

    public ChatSession withLastActivity(Instant activityAt) {
        return new ChatSession(sessionId, currentCharacter, startedAt, activityAt, status);
    }

    public ChatSession expired() {
        return new ChatSession(sessionId, currentCharacter, startedAt, lastActivity, SessionStatus.EXPIRED);
    }
}
