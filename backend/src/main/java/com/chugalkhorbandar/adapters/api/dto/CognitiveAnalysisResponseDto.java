package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;
import java.util.List;

public record CognitiveAnalysisResponseDto(
        String analysisId,
        String characterId,
        String conversationId,
        String provider,
        String model,
        long latencyMs,
        double confidence,
        Instant createdAt,
        List<ObservationResponseDto> observations,
        List<RecommendationResponseDto> recommendations,
        String rawJson) {}
