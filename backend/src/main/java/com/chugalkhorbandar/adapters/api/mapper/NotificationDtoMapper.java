package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.NotificationGenerationResponseDto;
import com.chugalkhorbandar.adapters.api.dto.NotificationGenerationTraceEntryDto;
import com.chugalkhorbandar.adapters.api.dto.NotificationResponseDto;
import com.chugalkhorbandar.application.notification.Notification;
import com.chugalkhorbandar.application.notification.NotificationGenerationSnapshot;
import com.chugalkhorbandar.application.notification.NotificationGenerationTraceEntry;

public final class NotificationDtoMapper {

    private NotificationDtoMapper() {}

    public static NotificationResponseDto toDto(Notification notification) {
        return new NotificationResponseDto(
                notification.id(),
                notification.recipientCharacterId(),
                notification.type(),
                notification.priority(),
                notification.title(),
                notification.summary(),
                notification.status(),
                notification.createdAt(),
                notification.expiresAt(),
                notification.source(),
                notification.trigger(),
                notification.metadata());
    }

    public static NotificationGenerationResponseDto toDto(NotificationGenerationSnapshot snapshot) {
        return new NotificationGenerationResponseDto(
                snapshot.characterId(),
                snapshot.generatedAt(),
                snapshot.trace().stream().map(NotificationDtoMapper::toDto).toList(),
                snapshot.generatedNotifications().stream().map(NotificationDtoMapper::toDto).toList());
    }

    private static NotificationGenerationTraceEntryDto toDto(NotificationGenerationTraceEntry entry) {
        return new NotificationGenerationTraceEntryDto(entry.rule(), entry.reason());
    }
}
