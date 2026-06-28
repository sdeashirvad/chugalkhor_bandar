package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.ports.ConversationRepository;
import java.time.Instant;
import java.util.Optional;

public final class InMemoryConversationRepository implements ConversationRepository {

    private final InMemoryConversationStore store;

    public InMemoryConversationRepository(InMemoryConversationStore store) {
        this.store = store;
    }

    @Override
    public Conversation save(Conversation conversation) {
        return store.save(conversation);
    }

    @Override
    public Optional<Conversation> findById(String conversationId) {
        return store.findById(conversationId);
    }

    @Override
    public Optional<Conversation> findActiveBySessionId(String sessionId) {
        return store.findActiveBySessionId(sessionId);
    }

    @Override
    public void updateActivity(String conversationId, Instant lastActivity) {
        store.updateActivity(conversationId, lastActivity);
    }

    @Override
    public void updateStatus(String conversationId, ConversationStatus status) {
        store.updateStatus(conversationId, status);
    }
}
