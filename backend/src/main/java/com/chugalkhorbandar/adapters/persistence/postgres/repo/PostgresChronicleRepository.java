package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ChronicleEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ChronicleJpaRepository;
import com.chugalkhorbandar.application.chronicle.Chronicle;
import com.chugalkhorbandar.application.chronicle.ChronicleCategory;
import com.chugalkhorbandar.application.chronicle.ChronicleConfidence;
import com.chugalkhorbandar.application.chronicle.ChronicleProvenance;
import com.chugalkhorbandar.application.chronicle.ChronicleVisibility;
import com.chugalkhorbandar.domain.chronicle.ports.ChronicleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("postgres-dev")
public class PostgresChronicleRepository implements ChronicleRepository {

    private final ChronicleJpaRepository jpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public PostgresChronicleRepository(ChronicleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Chronicle save(Chronicle chronicle) {
        jpaRepository.save(toEntity(chronicle));
        return chronicle;
    }

    @Override
    public Optional<Chronicle> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Chronicle> findLatestByCandidateId(String candidateId) {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDomain)
                .filter(chronicle -> candidateId.equals(chronicle.provenance().candidateId()))
                .max(Comparator.comparingInt(Chronicle::version));
    }

    @Override
    public int countByCandidateId(String candidateId) {
        return (int) jpaRepository.countByCandidateId(candidateId);
    }

    @Override
    public List<Chronicle> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Chronicle> findByCategoryOrderByCreatedAtDesc(ChronicleCategory category) {
        return jpaRepository.findByCategoryOrderByCreatedAtDesc(category.name()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Chronicle> findByVisibilityOrderByCreatedAtDesc(ChronicleVisibility visibility) {
        return jpaRepository.findByVisibilityOrderByCreatedAtDesc(visibility.name()).stream()
                .map(this::toDomain)
                .toList();
    }

    private Chronicle toDomain(ChronicleEntity entity) {
        return new Chronicle(
                entity.getId(),
                entity.getTitle(),
                ChronicleCategory.valueOf(entity.getCategory()),
                ChronicleVisibility.valueOf(entity.getVisibility()),
                ChronicleConfidence.valueOf(entity.getConfidence()),
                entity.getOwnerCharacterId(),
                entity.getSummary(),
                entity.getBody(),
                entity.getCreatedAt(),
                entity.getChronicleDate(),
                readMap(entity.getMetadataJson()),
                readProvenance(entity.getProvenanceJson()),
                entity.getVersion());
    }

    private ChronicleEntity toEntity(Chronicle chronicle) {
        return new ChronicleEntity(
                chronicle.id(),
                chronicle.title(),
                chronicle.category().name(),
                chronicle.visibility().name(),
                chronicle.confidence().name(),
                chronicle.ownerCharacterId(),
                chronicle.summary(),
                chronicle.body(),
                chronicle.createdAt(),
                chronicle.chronicleDate(),
                writeJson(chronicle.metadata()),
                writeJson(chronicle.provenance()),
                chronicle.version());
    }

    private Map<String, String> readMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception exception) {
            return Map.of();
        }
    }

    private ChronicleProvenance readProvenance(String json) {
        try {
            return objectMapper.readValue(json, ChronicleProvenance.class);
        } catch (Exception exception) {
            return new ChronicleProvenance("", List.of(), List.of(), List.of(), "", "", "", List.of(), Map.of());
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            return "{}";
        }
    }
}
