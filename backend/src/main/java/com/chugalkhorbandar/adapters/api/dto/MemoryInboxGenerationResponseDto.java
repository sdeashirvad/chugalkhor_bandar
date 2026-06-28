package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxGenerationTraceEntry;
import java.time.Instant;
import java.util.List;

public record MemoryInboxGenerationResponseDto(
        String characterId,
        String conversationId,
        Instant generatedAt,
        List<MemoryInboxGenerationTraceEntry> trace,
        List<MemoryInboxItemResponseDto> generatedItems) {}
