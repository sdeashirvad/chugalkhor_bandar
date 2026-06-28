package com.chugalkhorbandar.application.cognition;

import org.springframework.stereotype.Component;

@Component
public class CognitiveAnalysisProviderRegistry {

    private final CognitiveAnalysisProperties properties;
    private final MockCognitiveAnalysisProvider mockProvider;
    private final GroqCognitiveAnalysisProvider groqProvider;

    public CognitiveAnalysisProviderRegistry(
            CognitiveAnalysisProperties properties,
            MockCognitiveAnalysisProvider mockProvider,
            GroqCognitiveAnalysisProvider groqProvider) {
        this.properties = properties;
        this.mockProvider = mockProvider;
        this.groqProvider = groqProvider;
    }

    public CognitiveAnalysisProvider activeProvider() {
        if (properties.isMockEnabled() || "mock".equalsIgnoreCase(properties.getProvider())) {
            return mockProvider;
        }
        if ("groq".equalsIgnoreCase(properties.getProvider())) {
            if (groqProvider.isAvailable()) {
                return groqProvider;
            }
            if (properties.isMockEnabled()) {
                return mockProvider;
            }
            return groqProvider;
        }
        return mockProvider;
    }
}
