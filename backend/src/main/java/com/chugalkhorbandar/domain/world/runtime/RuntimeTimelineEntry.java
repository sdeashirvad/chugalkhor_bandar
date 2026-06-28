package com.chugalkhorbandar.domain.world.runtime;

import com.chugalkhorbandar.domain.world.commands.TimelineEntry;
import java.util.List;
import java.util.Map;

public record RuntimeTimelineEntry(
        String id,
        String chronologyId,
        String title,
        List<TimelineEntry> timelineEntries,
        Map<String, String> sections) {

    public RuntimeTimelineEntry {
        timelineEntries = List.copyOf(timelineEntries);
        sections = Map.copyOf(sections);
    }
}
