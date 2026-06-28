package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;
import java.util.Map;

public record WorldStatusDto(
        String status,
        String bootstrapVersion,
        Instant bootstrapTimestamp,
        Instant runtimeStartedAt,
        String persistenceProvider,
        int characters,
        int stories,
        int territories,
        int places,
        int organizations,
        int relationships,
        int timelineEntries,
        Map<String, Integer> charactersBySpecies,
        Map<String, Integer> storiesByEra) {}
