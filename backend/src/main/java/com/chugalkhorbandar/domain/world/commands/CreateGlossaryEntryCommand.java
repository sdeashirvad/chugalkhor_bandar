package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;
import java.util.Map;

public record CreateGlossaryEntryCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String glossaryId,
        String title,
        Map<String, String> sections)
        implements WorldCommand {

    public CreateGlossaryEntryCommand {
        sections = Map.copyOf(sections);
    }

    @Override
    public String commandType() {
        return "CreateGlossaryEntry";
    }
}
