package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;

public record DeleteCharacterCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String characterId)
        implements WorldCommand {

    @Override
    public String commandType() {
        return "DeleteCharacter";
    }
}
