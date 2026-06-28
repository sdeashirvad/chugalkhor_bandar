package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;

public record LinkStoryCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String storyId,
        String linkedStoryId,
        String linkType)
        implements WorldCommand {

    @Override
    public String commandType() {
        return "LinkStory";
    }
}
