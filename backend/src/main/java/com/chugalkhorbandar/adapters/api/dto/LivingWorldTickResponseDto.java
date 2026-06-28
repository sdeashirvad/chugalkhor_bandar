package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record LivingWorldTickResponseDto(
        String runId,
        String mode,
        Instant startedAt,
        Instant completedAt,
        long durationMs,
        LocalDate worldDate,
        int eventsGenerated,
        int artifactsGenerated,
        int notificationsGenerated,
        List<WorldEventResponseDto> events,
        List<String> artifactIds,
        List<String> notificationIds,
        List<LivingWorldTraceEntryResponseDto> trace) {}
