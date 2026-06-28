package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.BehaviorPlanningTraceEntryDto;
import com.chugalkhorbandar.adapters.api.dto.BehaviorProfileResponseDto;
import com.chugalkhorbandar.application.behavior.BehaviorPlanningTraceEntry;
import com.chugalkhorbandar.application.behavior.BehaviorProfileSnapshot;

public final class BehaviorDtoMapper {

    private BehaviorDtoMapper() {}

    public static BehaviorProfileResponseDto toDto(BehaviorProfileSnapshot snapshot) {
        return new BehaviorProfileResponseDto(
                snapshot.sessionId(),
                snapshot.profile().openingStyle(),
                snapshot.profile().narrationStyle(),
                snapshot.profile().humorLevel(),
                snapshot.profile().curiosityLevel(),
                snapshot.profile().endingStyle(),
                snapshot.profile().conversationFlavor(),
                snapshot.profile().energyModifier(),
                snapshot.profile().storytellingPreference(),
                snapshot.profile().createdAt(),
                snapshot.trace().entries().stream().map(BehaviorDtoMapper::toDto).toList());
    }

    private static BehaviorPlanningTraceEntryDto toDto(BehaviorPlanningTraceEntry entry) {
        return new BehaviorPlanningTraceEntryDto(entry.rule(), entry.reason());
    }
}
