package com.chugalkhorbandar.application.chronicle;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

public record Chronicle(
        String id,
        String title,
        ChronicleCategory category,
        ChronicleVisibility visibility,
        ChronicleConfidence confidence,
        String ownerCharacterId,
        String summary,
        String body,
        Instant createdAt,
        LocalDate chronicleDate,
        Map<String, String> metadata,
        ChronicleProvenance provenance,
        int version) {

    public Chronicle {
        title = title == null ? "" : title;
        summary = summary == null ? "" : summary;
        body = body == null ? "" : body;
        ownerCharacterId = ownerCharacterId == null ? "" : ownerCharacterId;
        metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
        version = Math.max(1, version);
    }
}
