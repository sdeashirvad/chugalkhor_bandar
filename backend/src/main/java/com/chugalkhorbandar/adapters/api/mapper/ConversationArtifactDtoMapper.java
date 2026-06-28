package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.ConversationArtifactGenerationResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ConversationArtifactResponseDto;
import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactGenerationSnapshot;

public final class ConversationArtifactDtoMapper {

    private ConversationArtifactDtoMapper() {}

    public static ConversationArtifactResponseDto toDto(ConversationArtifact artifact) {
        return new ConversationArtifactResponseDto(
                artifact.id(),
                artifact.type(),
                artifact.ownerCharacterId(),
                artifact.recipientCharacterId(),
                artifact.createdByCharacterId(),
                artifact.conversationId(),
                artifact.title(),
                artifact.summary(),
                artifact.status(),
                artifact.priority(),
                artifact.createdAt(),
                artifact.updatedAt(),
                artifact.expiresAt(),
                artifact.metadata(),
                artifact.trace());
    }

    public static ConversationArtifactGenerationResponseDto toDto(ConversationArtifactGenerationSnapshot snapshot) {
        return new ConversationArtifactGenerationResponseDto(
                snapshot.characterId(),
                snapshot.conversationId(),
                snapshot.generatedAt(),
                snapshot.trace(),
                snapshot.generatedArtifacts().stream()
                        .map(ConversationArtifactDtoMapper::toDto)
                        .toList());
    }
}
