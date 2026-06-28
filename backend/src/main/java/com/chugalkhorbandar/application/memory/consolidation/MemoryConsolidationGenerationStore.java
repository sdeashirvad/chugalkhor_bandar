package com.chugalkhorbandar.application.memory.consolidation;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryConsolidationGenerationStore {

    private final ConcurrentHashMap<String, MemoryConsolidationExecutionSnapshot> latestByRunId =
            new ConcurrentHashMap<>();
    private volatile MemoryConsolidationExecutionSnapshot latest;

    public void save(MemoryConsolidationExecutionSnapshot snapshot) {
        latest = snapshot;
        latestByRunId.put(snapshot.runId(), snapshot);
    }

    public Optional<MemoryConsolidationExecutionSnapshot> getLatest() {
        return Optional.ofNullable(latest);
    }
}
