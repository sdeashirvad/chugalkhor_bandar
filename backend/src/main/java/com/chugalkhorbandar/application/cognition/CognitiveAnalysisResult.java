package com.chugalkhorbandar.application.cognition;

import java.time.Instant;
import java.util.List;

public record CognitiveAnalysisResult(
        String analysisId,
        String characterId,
        String conversationId,
        String provider,
        String model,
        long latencyMs,
        double confidence,
        Instant createdAt,
        List<Observation> observations,
        List<Recommendation> recommendations,
        String rawJson) {

    public CognitiveAnalysisResult {
        observations = List.copyOf(observations == null ? List.of() : observations);
        recommendations = List.copyOf(recommendations == null ? List.of() : recommendations);
        rawJson = rawJson == null ? "" : rawJson;
    }
}
