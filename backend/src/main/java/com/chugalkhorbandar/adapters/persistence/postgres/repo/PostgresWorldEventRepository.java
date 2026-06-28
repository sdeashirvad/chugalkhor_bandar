package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.WorldEventEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.WorldEventJpaRepository;
import com.chugalkhorbandar.application.world.living.WorldEvent;
import com.chugalkhorbandar.application.world.living.WorldEventOrigin;
import com.chugalkhorbandar.application.world.living.WorldEventStatus;
import com.chugalkhorbandar.application.world.living.WorldEventType;
import com.chugalkhorbandar.application.world.living.WorldEventVisibility;
import com.chugalkhorbandar.domain.world.living.ports.WorldEventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("postgres-dev")
public class PostgresWorldEventRepository implements WorldEventRepository {

    private final WorldEventJpaRepository jpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public PostgresWorldEventRepository(WorldEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WorldEvent save(WorldEvent event) {
        jpaRepository.save(toEntity(event));
        return event;
    }

    @Override
    public Optional<WorldEvent> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<WorldEvent> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<WorldEvent> findByTypeOrderByCreatedAtDesc(WorldEventType type) {
        return jpaRepository.findByTypeOrderByCreatedAtDesc(type.name()).stream().map(this::toDomain).toList();
    }

    @Override
    public Set<String> findAllEventIds() {
        return new HashSet<>(jpaRepository.findAll().stream().map(WorldEventEntity::getId).toList());
    }

    private WorldEvent toDomain(WorldEventEntity entity) {
        return new WorldEvent(
                entity.getId(),
                WorldEventType.valueOf(entity.getType()),
                entity.getTitle(),
                entity.getSummary(),
                readList(entity.getParticipantsJson()),
                WorldEventVisibility.valueOf(entity.getVisibility()),
                entity.getCreatedAt(),
                entity.getEffectiveDate(),
                readMap(entity.getMetadataJson()),
                WorldEventStatus.valueOf(entity.getStatus()),
                WorldEventOrigin.valueOf(entity.getOrigin()));
    }

    private WorldEventEntity toEntity(WorldEvent event) {
        return new WorldEventEntity(
                event.id(),
                event.type().name(),
                event.title(),
                event.summary(),
                writeJson(event.participants()),
                event.visibility().name(),
                event.createdAt(),
                event.effectiveDate(),
                writeJson(event.metadata()),
                event.status().name(),
                event.origin().name());
    }

    private List<String> readList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception exception) {
            return List.of();
        }
    }

    private Map<String, String> readMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception exception) {
            return Map.of();
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            return "[]";
        }
    }
}
