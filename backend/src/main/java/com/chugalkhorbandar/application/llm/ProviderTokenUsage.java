package com.chugalkhorbandar.application.llm;

public record ProviderTokenUsage(int promptTokens, int completionTokens, int totalTokens) {}
