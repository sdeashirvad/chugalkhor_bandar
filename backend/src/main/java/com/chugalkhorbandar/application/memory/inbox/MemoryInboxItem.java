package com.chugalkhorbandar.application.memory.inbox;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record MemoryInboxItem(
        String id,
        String type,
        MemoryInboxSource source,
        String sourceId,
        String ownerCharacterId,
        String summary,
        MemoryInboxImportance importance,
        double confidence,
        MemoryInboxStatus status,
        Instant createdAt,
        Instant expiresAt,
        Map<String, String> metadata,
        List<String> trace,
        String analysisId,
        List<String> artifactIds) {

    public MemoryInboxItem {
        metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
        trace = List.copyOf(trace == null ? List.of() : trace);
        artifactIds = List.copyOf(artifactIds == null ? List.of() : artifactIds);
        analysisId = analysisId == null ? "" : analysisId;
    }

    public MemoryInboxItem withStatus(MemoryInboxStatus updatedStatus, Instant at) {
        return new MemoryInboxItem(
                id,
                type,
                source,
                sourceId,
                ownerCharacterId,
                summary,
                importance,
                confidence,
                updatedStatus,
                createdAt,
                at,
                metadata,
                trace,
                analysisId,
                artifactIds);
    }

    public MemoryInboxItem withTraceAppend(String event) {
        List<String> updated = new java.util.ArrayList<>(trace);
        updated.add(event);
        return new MemoryInboxItem(
                id,
                type,
                source,
                sourceId,
                ownerCharacterId,
                summary,
                importance,
                confidence,
                status,
                createdAt,
                expiresAt,
                metadata,
                updated,
                analysisId,
                artifactIds);
    }

    public MemoryInboxItem withMergedArtifactIds(List<String> additionalArtifactIds) {
        List<String> merged = new java.util.ArrayList<>(artifactIds);
        for (String artifactId : additionalArtifactIds) {
            if (!merged.contains(artifactId)) {
                merged.add(artifactId);
            }
        }
        return new MemoryInboxItem(
                id,
                type,
                source,
                sourceId,
                ownerCharacterId,
                summary,
                importance,
                confidence,
                status,
                createdAt,
                expiresAt,
                metadata,
                trace,
                analysisId,
                List.copyOf(merged));
    }
}
