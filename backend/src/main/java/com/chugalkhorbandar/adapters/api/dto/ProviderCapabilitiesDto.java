package com.chugalkhorbandar.adapters.api.dto;

public record ProviderCapabilitiesDto(
        int maxContextTokens,
        int reservedOutputTokens,
        int availablePromptTokens,
        boolean supportsSystemMessages,
        boolean supportsMultiMessage) {}
