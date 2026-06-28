package com.chugalkhorbandar.application.artifacts;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ConversationArtifact(
        String id,
        ConversationArtifactType type,
        String ownerCharacterId,
        String recipientCharacterId,
        String createdByCharacterId,
        String conversationId,
        String title,
        String summary,
        ConversationArtifactStatus status,
        ConversationArtifactPriority priority,
        Instant createdAt,
        Instant updatedAt,
        Instant expiresAt,
        Map<String, String> metadata,
        List<String> trace) {

    public ConversationArtifact {
        metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
        trace = List.copyOf(trace == null ? List.of() : trace);
    }

    public ConversationArtifact withStatus(ConversationArtifactStatus updatedStatus, Instant at) {
        return new ConversationArtifact(
                id,
                type,
                ownerCharacterId,
                recipientCharacterId,
                createdByCharacterId,
                conversationId,
                title,
                summary,
                updatedStatus,
                priority,
                createdAt,
                at,
                expiresAt,
                metadata,
                trace);
    }

    public ConversationArtifact withTraceAppend(String event) {
        List<String> updated = new java.util.ArrayList<>(trace);
        updated.add(event);
        return new ConversationArtifact(
                id,
                type,
                ownerCharacterId,
                recipientCharacterId,
                createdByCharacterId,
                conversationId,
                title,
                summary,
                status,
                priority,
                createdAt,
                updatedAt,
                expiresAt,
                metadata,
                updated);
    }
}
