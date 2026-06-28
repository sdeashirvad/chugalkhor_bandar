package com.chugalkhorbandar.domain.conversation.ports;

import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import java.time.Instant;
import java.util.Optional;

public interface ConversationRepository {

    Conversation save(Conversation conversation);

    Optional<Conversation> findById(String conversationId);

    Optional<Conversation> findActiveBySessionId(String sessionId);

    void updateActivity(String conversationId, Instant lastActivity);

    void updateStatus(String conversationId, ConversationStatus status);
}
