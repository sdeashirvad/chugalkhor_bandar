package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record RecordTimelineEntryCommand(
        String commandId,
        String correlationId,
        Instant createdAt,
        CommandSource source,
        String initiatedBy,
        String reason,
        CommandMetadata metadata,
        String chronologyId,
        String entryId,
        String title,
        List<TimelineEntry> timelineEntries,
        Map<String, String> sections)
        implements WorldCommand {

    public RecordTimelineEntryCommand {
        timelineEntries = List.copyOf(timelineEntries);
        sections = Map.copyOf(sections);
    }

    @Override
    public String commandType() {
        return "RecordTimelineEntry";
    }
}
