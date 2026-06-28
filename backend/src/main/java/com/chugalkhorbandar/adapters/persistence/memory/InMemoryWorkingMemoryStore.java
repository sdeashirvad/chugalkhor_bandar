package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.memory.working.WorkingMemoryFieldTrace;
import com.chugalkhorbandar.application.memory.working.WorkingMemorySnapshot;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryWorkingMemoryStore {

    private final ConcurrentHashMap<String, String> snapshotsBySession = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public Optional<WorkingMemorySnapshot> findBySessionId(String sessionId) {
        String json = snapshotsBySession.get(sessionId);
        if (json == null) {
            return Optional.empty();
        }
        try {
            StoredSnapshot stored = objectMapper.readValue(json, StoredSnapshot.class);
            return Optional.of(new WorkingMemorySnapshot(stored.memory(), stored.fieldTraces()));
        } catch (Exception exception) {
            snapshotsBySession.remove(sessionId);
            return Optional.empty();
        }
    }

    public WorkingMemorySnapshot save(WorkingMemorySnapshot snapshot) {
        try {
            String json = objectMapper.writeValueAsString(
                    new StoredSnapshot(snapshot.memory(), snapshot.fieldTraces()));
            snapshotsBySession.put(snapshot.memory().sessionId(), json);
            return snapshot;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to persist working memory snapshot", exception);
        }
    }

    public void deleteBySessionId(String sessionId) {
        if (sessionId != null) {
            snapshotsBySession.remove(sessionId);
        }
    }

    private record StoredSnapshot(
            com.chugalkhorbandar.application.memory.working.WorkingMemory memory,
            List<WorkingMemoryFieldTrace> fieldTraces) {}
}
