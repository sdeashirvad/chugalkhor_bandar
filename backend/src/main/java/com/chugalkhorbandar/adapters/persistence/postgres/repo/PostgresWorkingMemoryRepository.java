package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.WorkingMemoryEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.WorkingMemoryJpaRepository;
import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryFieldTrace;
import com.chugalkhorbandar.application.memory.working.WorkingMemorySnapshot;
import com.chugalkhorbandar.domain.memory.ports.WorkingMemoryRepository;
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
public class PostgresWorkingMemoryRepository implements WorkingMemoryRepository {

    private final WorkingMemoryJpaRepository jpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public PostgresWorkingMemoryRepository(WorkingMemoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<WorkingMemorySnapshot> findBySessionId(String sessionId) {
        return jpaRepository.findById(sessionId).map(this::toSnapshot);
    }

    @Override
    public WorkingMemorySnapshot save(WorkingMemorySnapshot snapshot) {
        WorkingMemory memory = snapshot.memory();
        WorkingMemoryEntity entity = new WorkingMemoryEntity(
                memory.sessionId(),
                memory.activeTopic(),
                memory.conversationMood(),
                memory.currentStory(),
                writeJson(memory.activeEntities()),
                writeJson(memory.unansweredQuestions()),
                writeJson(memory.recentPromises()),
                writeJson(memory.importantFacts()),
                writeJson(snapshot.fieldTraces()),
                memory.lastUpdated(),
                memory.version());
        jpaRepository.save(entity);
        return snapshot;
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        if (sessionId != null) {
            jpaRepository.deleteById(sessionId);
        }
    }

    private WorkingMemorySnapshot toSnapshot(WorkingMemoryEntity entity) {
        WorkingMemory memory = new WorkingMemory(
                entity.getSessionId(),
                entity.getActiveTopic(),
                entity.getConversationMood(),
                entity.getCurrentStory() == null ? "" : entity.getCurrentStory(),
                readStringList(entity.getActiveEntitiesJson()),
                readStringList(entity.getUnansweredQuestionsJson()),
                readStringList(entity.getRecentPromisesJson()),
                readStringList(entity.getImportantFactsJson()),
                entity.getLastUpdated(),
                entity.getVersion());
        return new WorkingMemorySnapshot(memory, readFieldTraces(entity.getFieldTracesJson()));
    }

    private List<String> readStringList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception exception) {
            return List.of();
        }
    }

    private List<WorkingMemoryFieldTrace> readFieldTraces(String json) {
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
            throw new IllegalStateException("Failed to serialize working memory payload", exception);
        }
    }
}
