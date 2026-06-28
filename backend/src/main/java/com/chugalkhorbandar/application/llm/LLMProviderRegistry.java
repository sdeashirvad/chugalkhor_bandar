package com.chugalkhorbandar.application.llm;

import com.chugalkhorbandar.application.llm.groq.GroqProvider;
import com.chugalkhorbandar.config.LlmProperties;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LLMProviderRegistry {

    private final LlmProperties llmProperties;
    private final Map<LLMProviderType, LLMProvider> providers;

    public LLMProviderRegistry(LlmProperties llmProperties, MockLLMProvider mockLLMProvider, GroqProvider groqProvider) {
        this.llmProperties = llmProperties;
        this.providers = new EnumMap<>(LLMProviderType.class);
        this.providers.put(LLMProviderType.MOCK, mockLLMProvider);
        this.providers.put(LLMProviderType.GROQ, groqProvider);
    }

    public LLMProvider activeProvider() {
        LLMProviderType configured = configuredType();
        LLMProvider provider = providers.get(configured);
        if (provider == null) {
            throw new UnsupportedOperationException("LLM provider not implemented: " + configured);
        }
        return provider;
    }

    public LLMProviderType configuredType() {
        return LLMProviderType.fromConfigValue(llmProperties.getProvider());
    }

    public List<LLMProviderType> implementedProviders() {
        return List.copyOf(providers.keySet());
    }

    public List<LLMProviderType> plannedProviders() {
        return List.of(LLMProviderType.values());
    }
}
