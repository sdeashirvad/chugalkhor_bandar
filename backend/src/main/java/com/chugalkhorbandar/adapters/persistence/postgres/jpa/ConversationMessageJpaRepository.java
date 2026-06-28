package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ConversationMessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationMessageJpaRepository extends JpaRepository<ConversationMessageEntity, String> {

    List<ConversationMessageEntity> findByConversationIdOrderBySequenceOrderAscMessageTimestampAsc(String conversationId);

    int countByConversationId(String conversationId);
}
