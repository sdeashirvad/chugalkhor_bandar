package com.chugalkhorbandar.application.cognition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class MockCognitiveAnalysisProvider implements CognitiveAnalysisProvider {

    private static final String MODEL = "mock-cognitive-analysis";
    private final ObjectMapper objectMapper;

    public MockCognitiveAnalysisProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String providerName() {
        return "mock";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public CognitiveAnalysisProviderResponse analyzeConversation(CognitiveAnalysisInput input) {
        long start = System.nanoTime();
        String rawJson = buildFixture(input);
        long latencyMs = Math.max(1, (System.nanoTime() - start) / 1_000_000);
        return new CognitiveAnalysisProviderResponse(providerName(), MODEL, latencyMs, rawJson);
    }

    private String buildFixture(CognitiveAnalysisInput input) {
        int hash = Math.abs(input.conversation().conversationId().hashCode());
        double observationConfidence = 0.85 + (hash % 10) / 100.0;
        double recommendationConfidence = 0.80 + (hash % 15) / 100.0;
        String characterName = input.currentUser().displayName();
        String latestMessage = input.transcript().isEmpty()
                ? ""
                : input.transcript().getLast().content().toLowerCase(Locale.ROOT);

        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode observations = root.putArray("observations");
        ObjectNode observation = observations.addObject();
        observation.put("type", detectObservationType(latestMessage).name());
        observation.put("confidence", observationConfidence);
        observation.put("summary", characterName + " engaged in a completed conversation turn.");
        observation.put("evidence", latestMessage.isBlank() ? "No user message captured." : latestMessage);

        ArrayNode recommendations = root.putArray("recommendations");
        ObjectNode recommendation = recommendations.addObject();
        recommendation.put("action", detectRecommendationAction(latestMessage).name());
        recommendation.put("confidence", recommendationConfidence);
        recommendation.put("reason", "Deterministic mock recommendation for developer inspection.");
        recommendation.put("target", input.conversation().conversationId());

        try {
            return objectMapper.writeValueAsString(root);
        } catch (Exception exception) {
            return "{\"observations\":[],\"recommendations\":[]}";
        }
    }

    private static ObservationType detectObservationType(String message) {
        if (message.contains("remind")) {
            return ObservationType.REMINDER;
        }
        if (message.contains("story")) {
            return ObservationType.STORY_SEED;
        }
        if (message.contains("remember")) {
            return ObservationType.PROMISE;
        }
        if (message.contains("?")) {
            return ObservationType.OPEN_QUESTION;
        }
        return ObservationType.PREFERENCE;
    }

    private static RecommendationAction detectRecommendationAction(String message) {
        if (message.contains("remember") || message.contains("remind")) {
            return RecommendationAction.PROMOTE_TO_MEMORY;
        }
        if (message.contains("story")) {
            return RecommendationAction.MERGE_ARTIFACT;
        }
        return RecommendationAction.WAIT;
    }
}
