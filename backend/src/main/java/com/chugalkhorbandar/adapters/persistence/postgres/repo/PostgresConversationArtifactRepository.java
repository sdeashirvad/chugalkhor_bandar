package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ConversationArtifactEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ConversationArtifactJpaRepository;
import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactPriority;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.domain.artifacts.ports.ConversationArtifactRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("postgres-dev")
public class PostgresConversationArtifactRepository implements ConversationArtifactRepository {

    private final ConversationArtifactJpaRepository jpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public PostgresConversationArtifactRepository(ConversationArtifactJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ConversationArtifact> findRelevantForCharacter(String characterId) {
        return jpaRepository
                .findByOwnerCharacterIdOrRecipientCharacterIdOrderByCreatedAtDesc(characterId, characterId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ConversationArtifact> findAllForCharacter(String characterId) {
        return jpaRepository
                .findByOwnerCharacterIdOrRecipientCharacterIdOrCreatedByCharacterIdOrderByCreatedAtDesc(
                        characterId, characterId, characterId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<ConversationArtifact> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public ConversationArtifact save(ConversationArtifact artifact) {
        jpaRepository.save(toEntity(artifact));
        return artifact;
    }

    private ConversationArtifact toDomain(ConversationArtifactEntity entity) {
        return new ConversationArtifact(
                entity.getId(),
                ConversationArtifactType.valueOf(entity.getType()),
                entity.getOwnerCharacterId(),
                entity.getRecipientCharacterId(),
                entity.getCreatedByCharacterId(),
                entity.getConversationId(),
                entity.getTitle(),
                entity.getSummary(),
                ConversationArtifactStatus.valueOf(entity.getStatus()),
                ConversationArtifactPriority.valueOf(entity.getPriority()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getExpiresAt(),
                readMetadata(entity.getMetadataJson()),
                readTrace(entity.getTraceJson()));
    }

    private ConversationArtifactEntity toEntity(ConversationArtifact artifact) {
        return new ConversationArtifactEntity(
                artifact.id(),
                artifact.type().name(),
                artifact.ownerCharacterId(),
                artifact.recipientCharacterId(),
                artifact.createdByCharacterId(),
                artifact.conversationId(),
                artifact.title(),
                artifact.summary(),
                artifact.status().name(),
                artifact.priority().name(),
                artifact.createdAt(),
                artifact.updatedAt(),
                artifact.expiresAt(),
                writeMetadata(artifact.metadata()),
                writeTrace(artifact.trace()));
    }

    private Map<String, String> readMetadata(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception exception) {
            return Map.of();
        }
    }

    private List<String> readTrace(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception exception) {
            return List.of();
        }
    }

    private String writeMetadata(Map<String, String> metadata) {
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to serialize artifact metadata", exception);
        }
    }

    private String writeTrace(List<String> trace) {
        try {
            return objectMapper.writeValueAsString(trace);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to serialize artifact trace", exception);
        }
    }
}
