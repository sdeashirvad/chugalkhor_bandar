package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.MemoryInboxGenerationResponseDto;
import com.chugalkhorbandar.adapters.api.dto.MemoryInboxItemResponseDto;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxGenerationSnapshot;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;

public final class MemoryInboxDtoMapper {

    private MemoryInboxDtoMapper() {}

    public static MemoryInboxItemResponseDto toDto(MemoryInboxItem item) {
        return new MemoryInboxItemResponseDto(
                item.id(),
                item.type(),
                item.source(),
                item.sourceId(),
                item.ownerCharacterId(),
                item.summary(),
                item.importance(),
                item.confidence(),
                item.status(),
                item.createdAt(),
                item.expiresAt(),
                item.metadata(),
                item.trace(),
                item.analysisId(),
                item.artifactIds());
    }

    public static MemoryInboxGenerationResponseDto toDto(MemoryInboxGenerationSnapshot snapshot) {
        return new MemoryInboxGenerationResponseDto(
                snapshot.characterId(),
                snapshot.conversationId(),
                snapshot.generatedAt(),
                snapshot.trace(),
                snapshot.generatedItems().stream().map(MemoryInboxDtoMapper::toDto).toList());
    }
}
