package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;
import java.util.Map;

public record CreateLawCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String lawId,
        String title,
        Map<String, String> sections)
        implements WorldCommand {

    public CreateLawCommand {
        sections = Map.copyOf(sections);
    }

    @Override
    public String commandType() {
        return "CreateLaw";
    }
}
