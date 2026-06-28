package com.chugalkhorbandar.adapters.api.dto;

public record ResolvedContextSectionDto(
        String type,
        int priority,
        String source,
        ContextReferenceDto reference,
        String contentReference,
        String content,
        int estimatedTokens) {}
