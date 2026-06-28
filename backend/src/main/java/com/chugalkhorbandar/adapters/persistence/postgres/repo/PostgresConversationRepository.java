package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ConversationEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ConversationJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ConversationMessageJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.ConversationMapper;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.ports.ConversationRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public final class PostgresConversationRepository implements ConversationRepository {

    private final ConversationJpaRepository conversationJpa;
    private final ConversationMessageJpaRepository messageJpa;

    public PostgresConversationRepository(
            ConversationJpaRepository conversationJpa, ConversationMessageJpaRepository messageJpa) {
        this.conversationJpa = conversationJpa;
        this.messageJpa = messageJpa;
    }

    @Override
    public Conversation save(Conversation conversation) {
        conversationJpa.save(ConversationMapper.toEntity(conversation));
        return hydrate(conversation.conversationId());
    }

    @Override
    public Optional<Conversation> findById(String conversationId) {
        return conversationJpa.findById(conversationId).map(entity -> hydrate(entity.getConversationId()));
    }

    @Override
    public Optional<Conversation> findActiveBySessionId(String sessionId) {
        return conversationJpa
                .findFirstBySessionIdAndStatusOrderByStartedAtDesc(sessionId, ConversationStatus.ACTIVE.name())
                .map(entity -> hydrate(entity.getConversationId()));
    }

    @Override
    public void updateActivity(String conversationId, Instant lastActivity) {
        conversationJpa.findById(conversationId).ifPresent(entity -> {
            entity.setLastActivity(lastActivity);
            conversationJpa.save(entity);
        });
    }

    @Override
    public void updateStatus(String conversationId, ConversationStatus status) {
        conversationJpa.findById(conversationId).ifPresent(entity -> {
            entity.setStatus(status.name());
            conversationJpa.save(entity);
        });
    }

    private Conversation hydrate(String conversationId) {
        ConversationEntity entity = conversationJpa
                .findById(conversationId)
                .orElseThrow(() -> new IllegalStateException("Missing conversation: " + conversationId));
        List<ConversationMessage> messages = messageJpa
                .findByConversationIdOrderBySequenceOrderAscMessageTimestampAsc(conversationId)
                .stream()
                .map(ConversationMapper::toDomain)
                .toList();
        return ConversationMapper.toDomain(entity, messages);
    }
}
