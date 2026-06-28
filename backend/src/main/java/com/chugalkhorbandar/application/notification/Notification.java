package com.chugalkhorbandar.application.notification;

import java.time.Instant;
import java.util.Map;

public record Notification(
        String id,
        String recipientCharacterId,
        NotificationType type,
        NotificationPriority priority,
        String title,
        String summary,
        NotificationStatus status,
        Instant createdAt,
        Instant expiresAt,
        String source,
        String trigger,
        Map<String, String> metadata) {

    public Notification {
        metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
    }

    public Notification withStatus(NotificationStatus updatedStatus) {
        return new Notification(
                id,
                recipientCharacterId,
                type,
                priority,
                title,
                summary,
                updatedStatus,
                createdAt,
                expiresAt,
                source,
                trigger,
                metadata);
    }
}
