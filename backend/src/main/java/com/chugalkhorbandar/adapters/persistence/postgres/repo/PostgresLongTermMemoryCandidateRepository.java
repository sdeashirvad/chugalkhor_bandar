package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.LongTermMemoryCandidateEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.LongTermMemoryCandidateJpaRepository;
import com.chugalkhorbandar.application.memory.consolidation.LongTermMemoryCandidate;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance;
import com.chugalkhorbandar.domain.memory.consolidation.ports.LongTermMemoryCandidateRepository;
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
public class PostgresLongTermMemoryCandidateRepository implements LongTermMemoryCandidateRepository {

    private final LongTermMemoryCandidateJpaRepository jpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public PostgresLongTermMemoryCandidateRepository(LongTermMemoryCandidateJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LongTermMemoryCandidate save(LongTermMemoryCandidate candidate) {
        jpaRepository.save(toEntity(candidate));
        return candidate;
    }

    @Override
    public Optional<LongTermMemoryCandidate> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<LongTermMemoryCandidate> findByRunId(String runId) {
        return jpaRepository.findByRunIdOrderByCreatedAtDesc(runId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<LongTermMemoryCandidate> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
    }

    private LongTermMemoryCandidate toDomain(LongTermMemoryCandidateEntity entity) {
        return new LongTermMemoryCandidate(
                entity.getId(),
                readList(entity.getSourceInboxItemIdsJson()),
                entity.getOwnerCharacterId(),
                entity.getSummary(),
                MemoryInboxImportance.valueOf(entity.getImportance()),
                entity.getReason(),
                entity.getCreatedAt(),
                entity.getRunId(),
                readMap(entity.getMetadataJson()));
    }

    private LongTermMemoryCandidateEntity toEntity(LongTermMemoryCandidate candidate) {
        return new LongTermMemoryCandidateEntity(
                candidate.id(),
                candidate.runId(),
                candidate.ownerCharacterId(),
                candidate.summary(),
                candidate.importance().name(),
                candidate.reason(),
                candidate.createdAt(),
                writeJson(candidate.sourceInboxItems()),
                writeJson(candidate.metadata()));
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
            throw new IllegalStateException("Failed to serialize consolidation payload", exception);
        }
    }
}
