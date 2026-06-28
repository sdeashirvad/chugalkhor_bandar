package com.chugalkhorbandar.application.cognition;

public record CognitiveAnalysisProviderResponse(
        String provider,
        String model,
        long latencyMs,
        String rawJson) {

    public CognitiveAnalysisProviderResponse {
        rawJson = rawJson == null ? "" : rawJson;
    }
}
