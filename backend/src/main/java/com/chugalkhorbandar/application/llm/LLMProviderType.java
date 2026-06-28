package com.chugalkhorbandar.application.llm;

public enum LLMProviderType {
    MOCK,
    GEMINI,
    OPENAI,
    CLAUDE,
    OLLAMA,
    OPENROUTER,
    GROQ;

    public static LLMProviderType fromConfigValue(String value) {
        if (value == null || value.isBlank()) {
            return MOCK;
        }
        return valueOf(value.trim().toUpperCase());
    }
}
