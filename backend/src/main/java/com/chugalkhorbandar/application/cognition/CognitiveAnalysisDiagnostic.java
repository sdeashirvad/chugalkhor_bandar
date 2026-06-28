package com.chugalkhorbandar.application.cognition;

import java.time.Instant;

public record CognitiveAnalysisDiagnostic(
        String id,
        String characterId,
        String conversationId,
        String provider,
        String errorMessage,
        long executionTimeMs,
        Instant createdAt) {}
