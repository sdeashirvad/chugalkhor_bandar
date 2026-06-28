package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;
import java.util.Map;

public record TerritoryDetailsDto(
        String id,
        String name,
        EntityReferenceDto ruler,
        List<EntityReferenceDto> ministers,
        List<EntityReferenceDto> places,
        Map<String, String> sections) {}
