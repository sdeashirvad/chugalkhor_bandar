package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;
import java.util.Map;

public record OrganizationDetailsDto(
        String id,
        String name,
        String type,
        EntityReferenceDto leader,
        EntityReferenceDto headquarters,
        List<EntityReferenceDto> members,
        Map<String, String> sections) {}
