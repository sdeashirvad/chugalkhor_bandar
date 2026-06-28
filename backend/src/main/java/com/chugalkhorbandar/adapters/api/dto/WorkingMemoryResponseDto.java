package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;
import java.util.List;

public record WorkingMemoryResponseDto(
        String sessionId,
        String activeTopic,
        String conversationMood,
        String currentStory,
        List<String> activeEntities,
        List<String> unansweredQuestions,
        List<String> recentPromises,
        List<String> importantFacts,
        Instant lastUpdated,
        long version,
        List<WorkingMemoryFieldTraceDto> fieldTraces) {}
