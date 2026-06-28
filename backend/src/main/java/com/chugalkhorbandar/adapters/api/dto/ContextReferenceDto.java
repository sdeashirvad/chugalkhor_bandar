package com.chugalkhorbandar.adapters.api.dto;

public record ContextReferenceDto(
        String provider, String entityType, String entityId, String attribute, int priority) {}
