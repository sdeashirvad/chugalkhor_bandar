package com.chugalkhorbandar.application.conversation.director;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ConversationExecutionRegistry {

    private final ConcurrentHashMap<String, ConversationExecutionHandle> active = new ConcurrentHashMap<>();

    public ConversationExecutionHandle begin(String sessionId) {
        cancelPending(sessionId, "Superseded by new user message");
        ConversationExecutionHandle handle = new ConversationExecutionHandle();
        active.put(sessionId, handle);
        return handle;
    }

    public Optional<String> cancelPending(String sessionId, String reason) {
        ConversationExecutionHandle handle = active.get(sessionId);
        if (handle != null && !handle.isCancelled()) {
            handle.cancel(reason);
            return Optional.of(reason);
        }
        return Optional.empty();
    }

    public void complete(String sessionId, ConversationExecutionHandle handle) {
        active.computeIfPresent(sessionId, (id, existing) -> existing == handle ? null : existing);
    }
}
