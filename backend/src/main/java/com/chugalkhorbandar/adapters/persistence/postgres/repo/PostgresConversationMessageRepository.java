package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ConversationMessageJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.ConversationMapper;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ports.ConversationMessageRepository;
import java.util.List;

public final class PostgresConversationMessageRepository implements ConversationMessageRepository {

    private final ConversationMessageJpaRepository messageJpa;

    public PostgresConversationMessageRepository(ConversationMessageJpaRepository messageJpa) {
        this.messageJpa = messageJpa;
    }

    @Override
    public ConversationMessage append(ConversationMessage message) {
        int sequenceOrder = messageJpa.countByConversationId(message.conversationId()) + 1;
        messageJpa.save(ConversationMapper.toEntity(message, sequenceOrder));
        return message;
    }

    @Override
    public List<ConversationMessage> findByConversationIdOrdered(String conversationId) {
        return messageJpa
                .findByConversationIdOrderBySequenceOrderAscMessageTimestampAsc(conversationId)
                .stream()
                .map(ConversationMapper::toDomain)
                .toList();
    }
}
