package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public final class InMemoryConversationStore {

    private record StoredMessage(int sequenceOrder, ConversationMessage message) {}

    private final ConcurrentHashMap<String, Conversation> conversations = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<StoredMessage>> messagesByConversation =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> sequenceByConversation = new ConcurrentHashMap<>();

    public Conversation save(Conversation conversation) {
        conversations.put(conversation.conversationId(), withoutMessages(conversation));
        messagesByConversation.putIfAbsent(conversation.conversationId(), new CopyOnWriteArrayList<>());
        sequenceByConversation.putIfAbsent(conversation.conversationId(), new AtomicInteger(0));
        return hydrate(conversation.conversationId());
    }

    public Optional<Conversation> findById(String conversationId) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            return Optional.empty();
        }
        return Optional.of(hydrate(conversationId));
    }

    public Optional<Conversation> findActiveBySessionId(String sessionId) {
        return conversations.values().stream()
                .filter(conversation -> conversation.sessionId().equals(sessionId))
                .filter(conversation -> conversation.status() == ConversationStatus.ACTIVE)
                .findFirst()
                .map(conversation -> hydrate(conversation.conversationId()));
    }

    public void updateActivity(String conversationId, Instant lastActivity) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation != null) {
            conversations.put(conversationId, conversation.withLastActivity(lastActivity));
        }
    }

    public void updateStatus(String conversationId, ConversationStatus status) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation != null) {
            Conversation updated = status == ConversationStatus.CLOSED ? conversation.closed() : conversation;
            conversations.put(conversationId, new Conversation(
                    updated.conversationId(),
                    updated.sessionId(),
                    updated.currentCharacter(),
                    updated.startedAt(),
                    updated.lastActivity(),
                    status,
                    List.of()));
        }
    }

    public ConversationMessage append(ConversationMessage message) {
        List<StoredMessage> bucket = messagesByConversation.computeIfAbsent(
                message.conversationId(), ignored -> new CopyOnWriteArrayList<>());
        AtomicInteger sequence = sequenceByConversation.computeIfAbsent(
                message.conversationId(), ignored -> new AtomicInteger(0));
        StoredMessage stored = new StoredMessage(sequence.incrementAndGet(), message);
        bucket.add(stored);
        return message;
    }

    public List<ConversationMessage> findByConversationIdOrdered(String conversationId) {
        List<StoredMessage> bucket = messagesByConversation.get(conversationId);
        if (bucket == null) {
            return List.of();
        }
        List<StoredMessage> ordered = new ArrayList<>(bucket);
        ordered.sort(Comparator.comparingInt(StoredMessage::sequenceOrder));
        return ordered.stream().map(StoredMessage::message).toList();
    }

    private Conversation hydrate(String conversationId) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            throw new IllegalStateException("Missing conversation: " + conversationId);
        }
        return conversation.withMessages(findByConversationIdOrdered(conversationId));
    }

    private static Conversation withoutMessages(Conversation conversation) {
        return new Conversation(
                conversation.conversationId(),
                conversation.sessionId(),
                conversation.currentCharacter(),
                conversation.startedAt(),
                conversation.lastActivity(),
                conversation.status(),
                List.of());
    }
}
