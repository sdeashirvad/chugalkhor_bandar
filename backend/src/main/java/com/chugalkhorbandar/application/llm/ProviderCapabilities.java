package com.chugalkhorbandar.application.llm;

public record ProviderCapabilities(
        int maxContextTokens,
        int reservedOutputTokens,
        boolean supportsSystemMessages,
        boolean supportsMultiMessage) {

    public int availablePromptTokens() {
        return Math.max(0, maxContextTokens - reservedOutputTokens);
    }
}
