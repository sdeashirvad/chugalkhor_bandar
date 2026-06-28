package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record LongTermMemoryCandidateResponseDto(
        String id,
        List<String> sourceInboxItems,
        String ownerCharacterId,
        String summary,
        MemoryInboxImportance importance,
        String reason,
        Instant createdAt,
        String runId,
        Map<String, String> metadata) {}
