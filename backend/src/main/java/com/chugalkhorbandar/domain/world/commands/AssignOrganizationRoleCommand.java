package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;

public record AssignOrganizationRoleCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String organizationId,
        String characterId,
        String role)
        implements WorldCommand {

    @Override
    public String commandType() {
        return "AssignOrganizationRole";
    }
}
