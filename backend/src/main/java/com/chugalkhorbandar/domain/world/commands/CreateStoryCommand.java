package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;
import java.util.Map;

public record CreateStoryCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String storyId,
        String title,
        Map<String, String> sections)
        implements WorldCommand {

    public CreateStoryCommand {
        sections = Map.copyOf(sections);
    }

    @Override
    public String commandType() {
        return "CreateStory";
    }
}
