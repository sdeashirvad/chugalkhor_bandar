package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;
import java.util.Map;

public record PlaceDetailsDto(
        String id,
        String name,
        String type,
        EntityReferenceDto territory,
        EntityReferenceDto owner,
        List<EntityReferenceDto> connectedPlaces,
        Map<String, String> sections) {}
