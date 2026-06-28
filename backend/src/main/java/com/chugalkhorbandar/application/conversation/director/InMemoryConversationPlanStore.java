package com.chugalkhorbandar.application.conversation.director;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryConversationPlanStore {

    private final ConcurrentHashMap<String, ConversationPlanSnapshot> snapshotsBySession = new ConcurrentHashMap<>();

    public Optional<ConversationPlanSnapshot> findBySessionId(String sessionId) {
        return Optional.ofNullable(snapshotsBySession.get(sessionId));
    }

    public ConversationPlanSnapshot save(ConversationPlanSnapshot snapshot) {
        snapshotsBySession.put(snapshot.sessionId(), snapshot);
        return snapshot;
    }

    public void deleteBySessionId(String sessionId) {
        if (sessionId != null) {
            snapshotsBySession.remove(sessionId);
        }
    }
}
