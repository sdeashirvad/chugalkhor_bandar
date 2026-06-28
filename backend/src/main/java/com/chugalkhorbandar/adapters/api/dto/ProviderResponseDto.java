package com.chugalkhorbandar.adapters.api.dto;

import java.util.Map;

public record ProviderResponseDto(
        String reply,
        ProviderTokenUsageDto tokenUsage,
        Map<String, String> providerMetadata,
        long latencyMs,
        String finishReason) {}
