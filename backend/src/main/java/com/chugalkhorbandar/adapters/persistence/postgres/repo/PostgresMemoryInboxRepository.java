package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.MemoryInboxItemEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.MemoryInboxItemJpaRepository;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxSource;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxStatus;
import com.chugalkhorbandar.domain.memory.inbox.ports.MemoryInboxRepository;
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
public class PostgresMemoryInboxRepository implements MemoryInboxRepository {

    private final MemoryInboxItemJpaRepository jpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public PostgresMemoryInboxRepository(MemoryInboxItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MemoryInboxItem save(MemoryInboxItem item) {
        jpaRepository.save(toEntity(item));
        return item;
    }

    @Override
    public Optional<MemoryInboxItem> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<MemoryInboxItem> findByOwnerCharacterIdAndSourceAndSourceId(
            String ownerCharacterId, String source, String sourceId) {
        return jpaRepository
                .findByOwnerCharacterIdAndSourceAndSourceId(ownerCharacterId, source, sourceId)
                .map(this::toDomain);
    }

    @Override
    public List<MemoryInboxItem> findByOwnerCharacterId(String ownerCharacterId) {
        return jpaRepository.findByOwnerCharacterIdOrderByCreatedAtDesc(ownerCharacterId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<MemoryInboxItem> findAll() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
    }

    private MemoryInboxItem toDomain(MemoryInboxItemEntity entity) {
        return new MemoryInboxItem(
                entity.getId(),
                entity.getType(),
                MemoryInboxSource.valueOf(entity.getSource()),
                entity.getSourceId(),
                entity.getOwnerCharacterId(),
                entity.getSummary(),
                MemoryInboxImportance.valueOf(entity.getImportance()),
                entity.getConfidence(),
                MemoryInboxStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                readMap(entity.getMetadataJson()),
                readList(entity.getTraceJson()),
                entity.getAnalysisId(),
                readList(entity.getArtifactIdsJson()));
    }

    private MemoryInboxItemEntity toEntity(MemoryInboxItem item) {
        return new MemoryInboxItemEntity(
                item.id(),
                item.type(),
                item.source().name(),
                item.sourceId(),
                item.ownerCharacterId(),
                item.summary(),
                item.importance().name(),
                item.confidence(),
                item.status().name(),
                item.createdAt(),
                item.expiresAt(),
                writeJson(item.metadata()),
                writeJson(item.trace()),
                item.analysisId(),
                writeJson(item.artifactIds()));
    }

    private Map<String, String> readMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception exception) {
            return Map.of();
        }
    }

    private List<String> readList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception exception) {
            return List.of();
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to serialize memory inbox payload", exception);
        }
    }
}
