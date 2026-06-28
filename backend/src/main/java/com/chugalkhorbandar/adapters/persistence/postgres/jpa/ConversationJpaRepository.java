package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ConversationEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationJpaRepository extends JpaRepository<ConversationEntity, String> {

    Optional<ConversationEntity> findFirstBySessionIdAndStatusOrderByStartedAtDesc(String sessionId, String status);
}
