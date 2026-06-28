package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;

public record TransferObjectCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String objectId,
        String fromOwnerId,
        String toOwnerId)
        implements WorldCommand {

    @Override
    public String commandType() {
        return "TransferObject";
    }
}
