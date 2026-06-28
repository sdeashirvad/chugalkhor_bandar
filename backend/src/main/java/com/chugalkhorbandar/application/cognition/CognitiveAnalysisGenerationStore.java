package com.chugalkhorbandar.application.cognition;

import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CognitiveAnalysisGenerationStore {

    private final java.util.concurrent.ConcurrentHashMap<String, CognitiveAnalysisExecutionSnapshot> latestByCharacterId =
            new java.util.concurrent.ConcurrentHashMap<>();

    public void saveSuccess(CognitiveAnalysisResult result, long executionTimeMs) {
        latestByCharacterId.put(
                result.characterId(),
                CognitiveAnalysisExecutionSnapshot.success(result, executionTimeMs, Instant.now()));
    }

    public void saveFailure(
            String characterId, String conversationId, String provider, String errorMessage, long executionTimeMs) {
        latestByCharacterId.put(
                characterId,
                CognitiveAnalysisExecutionSnapshot.failure(
                        characterId, conversationId, provider, errorMessage, executionTimeMs, Instant.now()));
    }

    public Optional<CognitiveAnalysisExecutionSnapshot> findByCharacterId(String characterId) {
        return Optional.ofNullable(latestByCharacterId.get(characterId));
    }
}
