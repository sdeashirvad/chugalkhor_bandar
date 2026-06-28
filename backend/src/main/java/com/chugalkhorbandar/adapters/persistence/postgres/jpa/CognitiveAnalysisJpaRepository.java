package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.CognitiveAnalysisEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CognitiveAnalysisJpaRepository extends JpaRepository<CognitiveAnalysisEntity, String> {

    Optional<CognitiveAnalysisEntity> findFirstByCharacterIdOrderByCreatedAtDesc(String characterId);

    Optional<CognitiveAnalysisEntity> findFirstByCharacterIdAndConversationIdOrderByCreatedAtDesc(
            String characterId, String conversationId);

    List<CognitiveAnalysisEntity> findByCharacterIdOrderByCreatedAtDesc(String characterId);
}
