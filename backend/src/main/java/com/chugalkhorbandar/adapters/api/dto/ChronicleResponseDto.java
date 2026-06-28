package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

public record ChronicleResponseDto(
        String id,
        String title,
        String category,
        String visibility,
        String confidence,
        String ownerCharacterId,
        String summary,
        String body,
        Instant createdAt,
        LocalDate chronicleDate,
        Map<String, String> metadata,
        ChronicleProvenanceResponseDto provenance,
        int version) {}
