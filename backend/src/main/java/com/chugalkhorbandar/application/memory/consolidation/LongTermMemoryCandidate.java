package com.chugalkhorbandar.application.memory.consolidation;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record LongTermMemoryCandidate(
        String id,
        List<String> sourceInboxItems,
        String ownerCharacterId,
        String summary,
        MemoryInboxImportance importance,
        String reason,
        Instant createdAt,
        String runId,
        Map<String, String> metadata) {

    public LongTermMemoryCandidate {
        sourceInboxItems = List.copyOf(sourceInboxItems == null ? List.of() : sourceInboxItems);
        metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
        reason = reason == null ? "" : reason;
        summary = summary == null ? "" : summary;
        runId = runId == null ? "" : runId;
    }

    public LongTermMemoryCandidate withRunId(String updatedRunId) {
        return new LongTermMemoryCandidate(
                id,
                sourceInboxItems,
                ownerCharacterId,
                summary,
                importance,
                reason,
                createdAt,
                updatedRunId,
                metadata);
    }
}
