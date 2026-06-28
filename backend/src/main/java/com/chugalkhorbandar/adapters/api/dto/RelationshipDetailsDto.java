package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;
import java.util.Map;

public record RelationshipDetailsDto(
        String id,
        String title,
        String relationshipType,
        String status,
        String description,
        List<EntityReferenceDto> characters) {}
