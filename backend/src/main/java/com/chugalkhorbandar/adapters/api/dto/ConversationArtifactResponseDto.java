package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.artifacts.ConversationArtifactPriority;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ConversationArtifactResponseDto(
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
        List<String> trace) {}
