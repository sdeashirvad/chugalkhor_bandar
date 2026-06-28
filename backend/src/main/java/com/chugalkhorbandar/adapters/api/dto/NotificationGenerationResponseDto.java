package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.notification.NotificationPriority;
import com.chugalkhorbandar.application.notification.NotificationType;
import java.time.Instant;
import java.util.List;

public record NotificationGenerationResponseDto(
        String characterId,
        Instant generatedAt,
        List<NotificationGenerationTraceEntryDto> trace,
        List<NotificationResponseDto> generatedNotifications) {}
