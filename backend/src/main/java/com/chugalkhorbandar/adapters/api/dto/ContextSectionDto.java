package com.chugalkhorbandar.adapters.api.dto;

public record ContextSectionDto(
        String type,
        int priority,
        String source,
        String contentReference,
        int estimatedTokens,
        ContextReferenceDto reference) {}
