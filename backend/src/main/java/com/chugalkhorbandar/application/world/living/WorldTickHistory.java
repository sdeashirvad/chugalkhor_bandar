package com.chugalkhorbandar.application.world.living;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record WorldTickHistory(
        String runId,
        WorldClockMode mode,
        Instant startedAt,
        Instant completedAt,
        long durationMs,
        LocalDate worldDate,
        int eventsGenerated,
        int artifactsGenerated,
        int notificationsGenerated,
        List<String> generatorNames,
        List<String> eventIds,
        List<String> artifactIds,
        List<String> notificationIds) {

    public WorldTickHistory {
        generatorNames = List.copyOf(generatorNames == null ? List.of() : generatorNames);
        eventIds = List.copyOf(eventIds == null ? List.of() : eventIds);
        artifactIds = List.copyOf(artifactIds == null ? List.of() : artifactIds);
        notificationIds = List.copyOf(notificationIds == null ? List.of() : notificationIds);
    }
}
