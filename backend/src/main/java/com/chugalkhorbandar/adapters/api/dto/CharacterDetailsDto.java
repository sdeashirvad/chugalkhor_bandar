package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;
import java.util.Map;

public record CharacterDetailsDto(
        String id,
        String name,
        String profile,
        List<String> titles,
        String history,
        String assets,
        List<RelationshipSummaryDto> relationships,
        Map<String, String> preferences,
        List<String> publicFacts,
        CurrentLocationDto currentLocation,
        EntityReferenceDto currentTerritory,
        List<OrganizationMembershipDto> organizations) {}
