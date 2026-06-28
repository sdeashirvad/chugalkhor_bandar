package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;

public record CurrentCharacterDto(
        String id,
        String displayName,
        List<String> titles,
        String species,
        String homeTerritory,
        String currentLocation) {}
