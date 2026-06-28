package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.WorldTickHistoryEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.WorldTickHistoryJpaRepository;
import com.chugalkhorbandar.application.world.living.WorldClockMode;
import com.chugalkhorbandar.application.world.living.WorldTickHistory;
import com.chugalkhorbandar.domain.world.living.ports.WorldTickHistoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("postgres-dev")
public class PostgresWorldTickHistoryRepository implements WorldTickHistoryRepository {

    private final WorldTickHistoryJpaRepository jpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public PostgresWorldTickHistoryRepository(WorldTickHistoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WorldTickHistory save(WorldTickHistory history) {
        jpaRepository.save(toEntity(history));
        return history;
    }

    @Override
    public Optional<WorldTickHistory> findLatest() {
        return jpaRepository.findAllByOrderByStartedAtDesc().stream().findFirst().map(this::toDomain);
    }

    @Override
    public Optional<WorldTickHistory> findLatestByMode(WorldClockMode mode) {
        return jpaRepository.findFirstByModeOrderByStartedAtDesc(mode.name()).map(this::toDomain);
    }

    @Override
    public List<WorldTickHistory> findAllOrderByStartedAtDesc() {
        return jpaRepository.findAllByOrderByStartedAtDesc().stream().map(this::toDomain).toList();
    }

    private WorldTickHistory toDomain(WorldTickHistoryEntity entity) {
        return new WorldTickHistory(
                entity.getRunId(),
                WorldClockMode.valueOf(entity.getMode()),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getDurationMs(),
                entity.getWorldDate(),
                entity.getEventsGenerated(),
                entity.getArtifactsGenerated(),
                entity.getNotificationsGenerated(),
                readList(entity.getGeneratorNamesJson()),
                readList(entity.getEventIdsJson()),
                readList(entity.getArtifactIdsJson()),
                readList(entity.getNotificationIdsJson()));
    }

    private WorldTickHistoryEntity toEntity(WorldTickHistory history) {
        return new WorldTickHistoryEntity(
                history.runId(),
                history.mode().name(),
                history.startedAt(),
                history.completedAt(),
                history.durationMs(),
                history.worldDate(),
                history.eventsGenerated(),
                history.artifactsGenerated(),
                history.notificationsGenerated(),
                writeJson(history.generatorNames()),
                writeJson(history.eventIds()),
                writeJson(history.artifactIds()),
                writeJson(history.notificationIds()));
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
            return "[]";
        }
    }
}
