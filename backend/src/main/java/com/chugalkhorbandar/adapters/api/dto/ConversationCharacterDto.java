package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;

public record ConversationCharacterDto(
        String id, String displayName, List<String> titles, String species, String homeTerritory) {}
