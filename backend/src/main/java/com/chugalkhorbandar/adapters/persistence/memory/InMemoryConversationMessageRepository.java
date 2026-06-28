package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ports.ConversationMessageRepository;
import java.util.List;

public final class InMemoryConversationMessageRepository implements ConversationMessageRepository {

    private final InMemoryConversationStore store;

    public InMemoryConversationMessageRepository(InMemoryConversationStore store) {
        this.store = store;
    }

    @Override
    public ConversationMessage append(ConversationMessage message) {
        return store.append(message);
    }

    @Override
    public List<ConversationMessage> findByConversationIdOrdered(String conversationId) {
        return store.findByConversationIdOrdered(conversationId);
    }
}
