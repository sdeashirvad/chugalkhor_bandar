package com.chugalkhorbandar.application.cognition;

import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CognitiveAnalysisEngine {

    private final CognitiveAnalysisProviderRegistry providerRegistry;
    private final CognitiveAnalysisJsonParser jsonParser;
    private final CognitiveAnalysisProperties properties;

    public CognitiveAnalysisEngine(
            CognitiveAnalysisProviderRegistry providerRegistry,
            CognitiveAnalysisJsonParser jsonParser,
            CognitiveAnalysisProperties properties) {
        this.providerRegistry = providerRegistry;
        this.jsonParser = jsonParser;
        this.properties = properties;
    }

    public CognitiveAnalysisResult analyze(String characterId, CognitiveAnalysisInput input) {
        Instant createdAt = Instant.now();
        CognitiveAnalysisProvider provider = providerRegistry.activeProvider();
        CognitiveAnalysisProviderResponse response = provider.analyzeConversation(input);
        CognitiveAnalysisJsonParser.ParsedAnalysis parsed =
                jsonParser.parse(response.rawJson(), properties.getMinimumConfidence(), createdAt);
        return new CognitiveAnalysisResult(
                UUID.randomUUID().toString(),
                characterId,
                input.conversation().conversationId(),
                response.provider(),
                response.model(),
                response.latencyMs(),
                parsed.confidence(),
                createdAt,
                parsed.observations(),
                parsed.recommendations(),
                parsed.rawJson());
    }
}
