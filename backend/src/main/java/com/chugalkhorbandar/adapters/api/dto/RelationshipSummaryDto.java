package com.chugalkhorbandar.adapters.api.dto;

public record RelationshipSummaryDto(
        String id,
        String title,
        String relationshipType,
        String status,
        EntityReferenceDto targetCharacter) {}
