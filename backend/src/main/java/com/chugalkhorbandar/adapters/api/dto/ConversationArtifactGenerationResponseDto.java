package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.artifacts.ConversationArtifactGenerationTraceEntry;
import java.time.Instant;
import java.util.List;

public record ConversationArtifactGenerationResponseDto(
        String characterId,
        String conversationId,
        Instant generatedAt,
        List<ConversationArtifactGenerationTraceEntry> trace,
        List<ConversationArtifactResponseDto> generatedArtifacts) {}
