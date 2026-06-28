package com.chugalkhorbandar.domain.conversation;

import java.time.Instant;
import java.util.List;

public record Conversation(
        String conversationId,
        String sessionId,
        ConversationCharacter currentCharacter,
        Instant startedAt,
        Instant lastActivity,
        ConversationStatus status,
        List<ConversationMessage> messages) {

    public Conversation withLastActivity(Instant activityAt) {
        return new Conversation(
                conversationId, sessionId, currentCharacter, startedAt, activityAt, status, messages);
    }

    public Conversation withMessages(List<ConversationMessage> updatedMessages) {
        return new Conversation(
                conversationId, sessionId, currentCharacter, startedAt, lastActivity, status, updatedMessages);
    }

    public Conversation closed() {
        return new Conversation(
                conversationId, sessionId, currentCharacter, startedAt, lastActivity, ConversationStatus.CLOSED, messages);
    }
}
