package com.chugalkhorbandar.application.memory.inbox;

import java.time.Instant;
import java.util.List;

public record MemoryInboxGenerationSnapshot(
        String characterId,
        String conversationId,
        Instant generatedAt,
        List<MemoryInboxGenerationTraceEntry> trace,
        List<MemoryInboxItem> generatedItems) {

    public MemoryInboxGenerationSnapshot {
        trace = List.copyOf(trace == null ? List.of() : trace);
        generatedItems = List.copyOf(generatedItems == null ? List.of() : generatedItems);
    }
}
