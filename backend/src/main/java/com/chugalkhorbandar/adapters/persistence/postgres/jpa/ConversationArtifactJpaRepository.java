package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ConversationArtifactEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationArtifactJpaRepository extends JpaRepository<ConversationArtifactEntity, String> {

    List<ConversationArtifactEntity> findByOwnerCharacterIdOrRecipientCharacterIdOrderByCreatedAtDesc(
            String ownerCharacterId, String recipientCharacterId);

    List<ConversationArtifactEntity> findByOwnerCharacterIdOrRecipientCharacterIdOrCreatedByCharacterIdOrderByCreatedAtDesc(
            String ownerCharacterId, String recipientCharacterId, String createdByCharacterId);
}
