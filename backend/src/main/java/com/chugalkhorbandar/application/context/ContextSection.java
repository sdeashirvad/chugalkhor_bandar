package com.chugalkhorbandar.application.context;

public record ContextSection(
        ContextSectionType type,
        int priority,
        String source,
        ContextReference reference,
        int estimatedTokens) {

    public String contentReference() {
        return reference.format();
    }

    public static int estimateTokens(ContextReference reference) {
        return estimateTokens(reference.format());
    }

    public static int estimateTokens(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Math.max(1, value.length() / 4);
    }

    public static int estimateTokensFromContent(String content) {
        return estimateTokens(content);
    }
}
