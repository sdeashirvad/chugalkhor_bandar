package com.chugalkhorbandar.application.cognition;

import java.time.Instant;
import java.util.Optional;

public record CognitiveAnalysisExecutionSnapshot(
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
        CognitiveAnalysisResult result) {

    public static CognitiveAnalysisExecutionSnapshot success(
            CognitiveAnalysisResult result, long executionTimeMs, Instant completedAt) {
        return new CognitiveAnalysisExecutionSnapshot(
                result.characterId(),
                result.conversationId(),
                true,
                result.provider(),
                result.model(),
                result.latencyMs(),
                executionTimeMs,
                result.confidence(),
                "",
                completedAt,
                result);
    }

    public static CognitiveAnalysisExecutionSnapshot failure(
            String characterId,
            String conversationId,
            String provider,
            String errorMessage,
            long executionTimeMs,
            Instant completedAt) {
        return new CognitiveAnalysisExecutionSnapshot(
                characterId,
                conversationId,
                false,
                provider,
                "",
                0,
                executionTimeMs,
                0,
                errorMessage,
                completedAt,
                null);
    }

    public Optional<CognitiveAnalysisResult> resultOptional() {
        return Optional.ofNullable(result);
    }
}
