package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;

public record ChangeTerritoryRulerCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String territoryId,
        String newRulerId)
        implements WorldCommand {

    @Override
    public String commandType() {
        return "ChangeTerritoryRuler";
    }
}
