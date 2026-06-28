package com.chugalkhorbandar.application.llm;

public interface LLMProvider {

    LLMProviderType providerType();

    ProviderResponse generateReply(ProviderRequest request);

    boolean health();

    LLMProviderInfo providerInfo();

    ProviderCapabilities capabilities();
}
