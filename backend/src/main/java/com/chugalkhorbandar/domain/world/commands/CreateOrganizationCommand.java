package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;
import java.util.Map;

public record CreateOrganizationCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String organizationId,
        String title,
        Map<String, String> sections)
        implements WorldCommand {

    public CreateOrganizationCommand {
        sections = Map.copyOf(sections);
    }

    @Override
    public String commandType() {
        return "CreateOrganization";
    }
}
