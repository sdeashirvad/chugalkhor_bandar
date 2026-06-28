package com.chugalkhorbandar.application.llm;

public record LLMGenerateResult(
        LLMProviderInfo providerInfo, ProviderRequest providerRequest, ProviderResponse providerResponse) {}
