package com.chugalkhorbandar.application.llm;

public record LLMProviderInfo(
        LLMProviderType type,
        String name,
        String description,
        boolean healthy,
        String model) {}
