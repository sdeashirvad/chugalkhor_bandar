package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record LivingWorldTickResult(
        String runId,
        WorldClockMode mode,
        Instant startedAt,
        Instant completedAt,
        long durationMs,
        LocalDate worldDate,
        int eventsGenerated,
        int artifactsGenerated,
        int notificationsGenerated,
        List<WorldEvent> events,
        List<ConversationArtifact> artifacts,
        List<String> artifactIds,
        List<String> notificationIds,
        List<LivingWorldTraceEntry> trace) {

    public LivingWorldTickResult {
        events = List.copyOf(events == null ? List.of() : events);
        artifacts = List.copyOf(artifacts == null ? List.of() : artifacts);
        artifactIds = List.copyOf(artifactIds == null ? List.of() : artifactIds);
        notificationIds = List.copyOf(notificationIds == null ? List.of() : notificationIds);
        trace = List.copyOf(trace == null ? List.of() : trace);
    }
}
