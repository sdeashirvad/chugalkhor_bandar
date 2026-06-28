package com.chugalkhorbandar.application.llm;

import java.util.List;
import java.util.Map;

public record ProviderRequest(
        List<ProviderMessage> messages,
        Map<String, String> metadata,
        double temperature,
        int maxOutputTokens,
        String model) {

    public ProviderRequest {
        messages = List.copyOf(messages);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
