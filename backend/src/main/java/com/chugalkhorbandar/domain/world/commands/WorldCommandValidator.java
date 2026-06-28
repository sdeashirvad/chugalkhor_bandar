package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;

final class WorldCommandValidator {

    private WorldCommandValidator() {}

    static void requireCommandId(String commandId) {
        requireNonBlank(commandId, "commandId");
    }

    static void requireCorrelationId(String correlationId) {
        requireNonBlank(correlationId, "correlationId");
    }

    static void requireCreatedAt(Instant createdAt) {
        if (createdAt == null) {
            throw new WorldCommandValidationException("createdAt is required");
        }
    }

    static void requireSource(CommandSource source) {
        if (source == null) {
            throw new WorldCommandValidationException("source is required");
        }
    }

    static void requireInitiatedBy(String initiatedBy) {
        requireNonBlank(initiatedBy, "initiatedBy");
    }

    static void requireReason(String reason) {
        requireNonBlank(reason, "reason");
    }

    static void requireMetadata(CommandMetadata metadata) {
        if (metadata == null) {
            throw new WorldCommandValidationException("metadata is required");
        }
    }

    static void requireEntityId(String entityId, String fieldName) {
        requireNonBlank(entityId, fieldName);
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new WorldCommandValidationException(fieldName + " is required");
        }
    }
}
