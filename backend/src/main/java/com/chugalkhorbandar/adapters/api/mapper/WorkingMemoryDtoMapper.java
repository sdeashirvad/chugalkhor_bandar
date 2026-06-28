package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.WorkingMemoryFieldTraceDto;
import com.chugalkhorbandar.adapters.api.dto.WorkingMemoryResponseDto;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryFieldTrace;
import com.chugalkhorbandar.application.memory.working.WorkingMemorySnapshot;

public final class WorkingMemoryDtoMapper {

    private WorkingMemoryDtoMapper() {}

    public static WorkingMemoryResponseDto toDto(WorkingMemorySnapshot snapshot) {
        return new WorkingMemoryResponseDto(
                snapshot.memory().sessionId(),
                snapshot.memory().activeTopic(),
                snapshot.memory().conversationMood(),
                snapshot.memory().currentStory(),
                snapshot.memory().activeEntities(),
                snapshot.memory().unansweredQuestions(),
                snapshot.memory().recentPromises(),
                snapshot.memory().importantFacts(),
                snapshot.memory().lastUpdated(),
                snapshot.memory().version(),
                snapshot.fieldTraces().stream().map(WorkingMemoryDtoMapper::toDto).toList());
    }

    private static WorkingMemoryFieldTraceDto toDto(WorkingMemoryFieldTrace trace) {
        return new WorkingMemoryFieldTraceDto(trace.field(), trace.value(), trace.heuristic());
    }
}
