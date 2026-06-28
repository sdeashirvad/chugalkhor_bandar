package com.chugalkhorbandar.application.llm;

import java.util.Map;

public record ProviderMessage(
        ProviderMessageRole role,
        String content,
        String sectionType,
        Map<String, String> metadata) {

    public ProviderMessage {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ProviderMessage of(ProviderMessageRole role, String content, String sectionType) {
        return new ProviderMessage(role, content, sectionType, Map.of());
    }
}
