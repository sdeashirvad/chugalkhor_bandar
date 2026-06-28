package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;
import java.util.List;

public record CharacterSummaryDto(
        String id,
        String name,
        String species,
        List<String> titles,
        String currentPlace,
        String currentPlaceName,
        Instant lastSeenAt) {}
