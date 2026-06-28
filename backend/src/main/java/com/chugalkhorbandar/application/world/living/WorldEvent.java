package com.chugalkhorbandar.application.world.living;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record WorldEvent(
        String id,
        WorldEventType type,
        String title,
        String summary,
        List<String> participants,
        WorldEventVisibility visibility,
        Instant createdAt,
        LocalDate effectiveDate,
        Map<String, String> metadata,
        WorldEventStatus status,
        WorldEventOrigin origin) {

    public WorldEvent {
        participants = List.copyOf(participants == null ? List.of() : participants);
        metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
        title = title == null ? "" : title;
        summary = summary == null ? "" : summary;
    }
}
