package com.chugalkhorbandar.application.cognition;

import java.time.Instant;
import java.util.Map;

public record Observation(
        String id,
        ObservationType type,
        double confidence,
        String summary,
        String evidence,
        Map<String, String> metadata,
        Instant createdAt) {

    public Observation {
        metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
        summary = summary == null ? "" : summary;
        evidence = evidence == null ? "" : evidence;
    }
}
