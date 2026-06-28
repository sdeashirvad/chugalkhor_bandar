package com.chugalkhorbandar.application.cognition;

import java.util.Map;

public record Recommendation(
        String id,
        RecommendationAction action,
        double confidence,
        String reason,
        String target,
        Map<String, String> metadata) {

    public Recommendation {
        metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
        reason = reason == null ? "" : reason;
        target = target == null ? "" : target;
    }
}
