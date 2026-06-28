package com.chugalkhorbandar.application.notification;

import java.time.Instant;
import java.util.List;

public record NotificationGenerationSnapshot(
        String characterId,
        Instant generatedAt,
        List<NotificationGenerationTraceEntry> trace,
        List<Notification> generatedNotifications) {

    public NotificationGenerationSnapshot {
        trace = List.copyOf(trace == null ? List.of() : trace);
        generatedNotifications = List.copyOf(generatedNotifications == null ? List.of() : generatedNotifications);
    }
}
