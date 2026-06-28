package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.LivingWorldTickResponseDto;
import com.chugalkhorbandar.adapters.api.dto.LivingWorldTraceEntryResponseDto;
import com.chugalkhorbandar.adapters.api.dto.WorldEventResponseDto;
import com.chugalkhorbandar.application.world.living.LivingWorldTickResult;
import com.chugalkhorbandar.application.world.living.LivingWorldTraceEntry;
import com.chugalkhorbandar.application.world.living.WorldEvent;

public final class LivingWorldDtoMapper {

    private LivingWorldDtoMapper() {}

    public static WorldEventResponseDto toDto(WorldEvent event) {
        return new WorldEventResponseDto(
                event.id(),
                event.type().name(),
                event.title(),
                event.summary(),
                event.participants(),
                event.visibility().name(),
                event.createdAt(),
                event.effectiveDate(),
                event.metadata(),
                event.status().name(),
                event.origin().name());
    }

    public static LivingWorldTraceEntryResponseDto toDto(LivingWorldTraceEntry entry) {
        return new LivingWorldTraceEntryResponseDto(entry.generator(), entry.rule(), entry.reason());
    }

    public static LivingWorldTickResponseDto toDto(LivingWorldTickResult result) {
        return new LivingWorldTickResponseDto(
                result.runId(),
                result.mode().name(),
                result.startedAt(),
                result.completedAt(),
                result.durationMs(),
                result.worldDate(),
                result.eventsGenerated(),
                result.artifactsGenerated(),
                result.notificationsGenerated(),
                result.events().stream().map(LivingWorldDtoMapper::toDto).toList(),
                result.artifactIds(),
                result.notificationIds(),
                result.trace().stream().map(LivingWorldDtoMapper::toDto).toList());
    }
}
