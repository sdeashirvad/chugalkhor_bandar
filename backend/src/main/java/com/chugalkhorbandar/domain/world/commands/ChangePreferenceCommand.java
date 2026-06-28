package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;

public record ChangePreferenceCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String characterId,
        String preferenceKey,
        String preferenceValue)
        implements WorldCommand {

    @Override
    public String commandType() {
        return "ChangePreference";
    }
}
