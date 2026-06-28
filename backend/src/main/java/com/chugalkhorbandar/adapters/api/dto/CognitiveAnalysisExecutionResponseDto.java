package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;

public record CognitiveAnalysisExecutionResponseDto(
        String characterId,
        String conversationId,
        boolean success,
        String provider,
        String model,
        long providerLatencyMs,
        long executionTimeMs,
        double confidence,
        String errorMessage,
        Instant completedAt,
        CognitiveAnalysisResponseDto result) {}
