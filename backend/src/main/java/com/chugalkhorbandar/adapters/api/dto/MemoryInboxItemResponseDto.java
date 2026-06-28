package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxSource;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxStatus;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record MemoryInboxItemResponseDto(
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
        List<String> artifactIds) {}
