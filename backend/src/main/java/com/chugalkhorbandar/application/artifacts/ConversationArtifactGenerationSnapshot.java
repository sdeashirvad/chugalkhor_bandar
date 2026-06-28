package com.chugalkhorbandar.application.artifacts;

import java.time.Instant;
import java.util.List;

public record ConversationArtifactGenerationSnapshot(
        String characterId,
        String conversationId,
        Instant generatedAt,
        List<ConversationArtifactGenerationTraceEntry> trace,
        List<ConversationArtifact> generatedArtifacts) {

    public ConversationArtifactGenerationSnapshot {
        trace = List.copyOf(trace == null ? List.of() : trace);
        generatedArtifacts = List.copyOf(generatedArtifacts == null ? List.of() : generatedArtifacts);
    }
}
