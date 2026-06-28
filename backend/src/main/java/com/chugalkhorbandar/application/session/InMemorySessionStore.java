package com.chugalkhorbandar.application.session;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class InMemorySessionStore {

    private final ConcurrentHashMap<String, ChatSession> sessions = new ConcurrentHashMap<>();
    private final List<SessionExpiredListener> sessionExpiredListeners;
    private volatile Duration inactivityTimeout = Duration.ofMinutes(30);

    public InMemorySessionStore(@Lazy List<SessionExpiredListener> sessionExpiredListeners) {
        this.sessionExpiredListeners = List.copyOf(sessionExpiredListeners);
    }

    public void setInactivityTimeout(Duration inactivityTimeout) {
        this.inactivityTimeout = inactivityTimeout;
    }

    public ChatSession create(CurrentCharacter character) {
        Instant now = Instant.now();
        String sessionId = UUID.randomUUID().toString();
        ChatSession session = new ChatSession(sessionId, character, now, now, SessionStatus.ACTIVE);
        sessions.put(sessionId, session);
        return session;
    }

    public Optional<ChatSession> find(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return Optional.empty();
        }
        ChatSession session = sessions.get(sessionId);
        if (session == null) {
            return Optional.empty();
        }
        if (isExpired(session)) {
            sessions.put(sessionId, session.expired());
            notifySessionEnded(sessionId);
            return Optional.empty();
        }
        return Optional.of(session);
    }

    public Optional<ChatSession> touch(String sessionId) {
        Optional<ChatSession> session = find(sessionId);
        if (session.isEmpty()) {
            return Optional.empty();
        }
        ChatSession active = session.get().withLastActivity(Instant.now());
        sessions.put(sessionId, active);
        return Optional.of(active);
    }

    public void remove(String sessionId) {
        if (sessionId != null) {
            sessions.remove(sessionId);
            notifySessionEnded(sessionId);
        }
    }

    public void register(ChatSession session) {
        sessions.put(session.sessionId(), session);
    }

    private boolean isExpired(ChatSession session) {
        return session.lastActivity().plus(inactivityTimeout).isBefore(Instant.now());
    }

    private void notifySessionEnded(String sessionId) {
        for (SessionExpiredListener listener : sessionExpiredListeners) {
            listener.onSessionExpired(sessionId);
        }
    }
}
