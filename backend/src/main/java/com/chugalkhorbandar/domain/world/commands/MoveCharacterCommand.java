package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;

public record MoveCharacterCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String characterId,
        String fromPlaceId,
        String toPlaceId)
        implements WorldCommand {

    @Override
    public String commandType() {
        return "MoveCharacter";
    }
}
