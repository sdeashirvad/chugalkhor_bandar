package com.chugalkhorbandar.application.memory.inbox;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import com.chugalkhorbandar.application.cognition.Observation;
import com.chugalkhorbandar.application.cognition.Recommendation;
import java.time.Instant;
import java.util.List;

public record MemoryInboxEngineInput(
        String ownerCharacterId,
        String conversationId,
        List<ConversationArtifact> artifacts,
        CognitiveAnalysisResult analysis,
        String runtimeWorldSummary,
        Instant currentTime,
        List<MemoryInboxItem> existingItems) {

    public MemoryInboxEngineInput {
        artifacts = List.copyOf(artifacts == null ? List.of() : artifacts);
        runtimeWorldSummary = runtimeWorldSummary == null ? "" : runtimeWorldSummary;
        existingItems = List.copyOf(existingItems == null ? List.of() : existingItems);
    }
}
