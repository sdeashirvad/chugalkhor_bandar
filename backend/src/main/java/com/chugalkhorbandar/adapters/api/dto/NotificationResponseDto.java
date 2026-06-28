package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.notification.NotificationPriority;
import com.chugalkhorbandar.application.notification.NotificationStatus;
import com.chugalkhorbandar.application.notification.NotificationType;
import java.time.Instant;
import java.util.Map;

public record NotificationResponseDto(
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
        Map<String, String> metadata) {}
