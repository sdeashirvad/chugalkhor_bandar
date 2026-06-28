package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;
import java.util.Map;

public record CreateRelationshipCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String relationshipId,
        String title,
        Map<String, String> sections)
        implements WorldCommand {

    public CreateRelationshipCommand {
        sections = Map.copyOf(sections);
    }

    @Override
    public String commandType() {
        return "CreateRelationship";
    }
}
