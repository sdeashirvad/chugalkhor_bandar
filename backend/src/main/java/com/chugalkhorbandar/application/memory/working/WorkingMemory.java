package com.chugalkhorbandar.application.memory.working;

import java.time.Instant;
import java.util.List;

public record WorkingMemory(
        String sessionId,
        String activeTopic,
        String conversationMood,
        String currentStory,
        List<String> activeEntities,
        List<String> unansweredQuestions,
        List<String> recentPromises,
        List<String> importantFacts,
        Instant lastUpdated,
        long version) {

    public WorkingMemory {
        activeEntities = List.copyOf(activeEntities == null ? List.of() : activeEntities);
        unansweredQuestions = List.copyOf(unansweredQuestions == null ? List.of() : unansweredQuestions);
        recentPromises = List.copyOf(recentPromises == null ? List.of() : recentPromises);
        importantFacts = List.copyOf(importantFacts == null ? List.of() : importantFacts);
        currentStory = currentStory == null ? "" : currentStory;
    }
}
