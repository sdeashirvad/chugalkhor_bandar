package com.chugalkhorbandar.application.llm;

import java.util.Map;

public record ProviderResponse(
        String reply,
        ProviderTokenUsage tokenUsage,
        Map<String, String> providerMetadata,
        long latencyMs,
        String finishReason) {

    public ProviderResponse {
        providerMetadata = providerMetadata == null ? Map.of() : Map.copyOf(providerMetadata);
    }
}
