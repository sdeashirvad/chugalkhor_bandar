package com.chugalkhorbandar.application.cognition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CognitiveAnalysisJsonParser {

    private final ObjectMapper objectMapper;

    public CognitiveAnalysisJsonParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ParsedAnalysis parse(String rawJson, double minimumConfidence, Instant createdAt) {
        try {
            String json = extractJson(rawJson);
            JsonNode root = objectMapper.readTree(json);
            List<Observation> observations = parseObservations(root.path("observations"), minimumConfidence, createdAt);
            List<Recommendation> recommendations =
                    parseRecommendations(root.path("recommendations"), minimumConfidence);
            double confidence = computeConfidence(observations, recommendations);
            return new ParsedAnalysis(observations, recommendations, confidence, json);
        } catch (Exception exception) {
            throw new CognitiveAnalysisParseException("Failed to parse cognitive analysis JSON", exception);
        }
    }

    private static List<Observation> parseObservations(JsonNode node, double minimumConfidence, Instant createdAt) {
        List<Observation> observations = new ArrayList<>();
        if (!node.isArray()) {
            return observations;
        }
        for (JsonNode entry : node) {
            double confidence = entry.path("confidence").asDouble(0);
            if (confidence < minimumConfidence) {
                continue;
            }
            ObservationType type = parseObservationType(entry.path("type").asText("UNKNOWN"));
            observations.add(new Observation(
                    UUID.randomUUID().toString(),
                    type,
                    confidence,
                    entry.path("summary").asText(""),
                    entry.path("evidence").asText(""),
                    java.util.Map.of(),
                    createdAt));
        }
        return observations;
    }

    private static List<Recommendation> parseRecommendations(JsonNode node, double minimumConfidence) {
        List<Recommendation> recommendations = new ArrayList<>();
        if (!node.isArray()) {
            return recommendations;
        }
        for (JsonNode entry : node) {
            double confidence = entry.path("confidence").asDouble(0);
            if (confidence < minimumConfidence) {
                continue;
            }
            RecommendationAction action = parseRecommendationAction(entry.path("action").asText("WAIT"));
            recommendations.add(new Recommendation(
                    UUID.randomUUID().toString(),
                    action,
                    confidence,
                    entry.path("reason").asText(""),
                    entry.path("target").asText(""),
                    java.util.Map.of()));
        }
        return recommendations;
    }

    private static ObservationType parseObservationType(String value) {
        try {
            return ObservationType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return ObservationType.UNKNOWN;
        }
    }

    private static RecommendationAction parseRecommendationAction(String value) {
        try {
            return RecommendationAction.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return RecommendationAction.WAIT;
        }
    }

    private static double computeConfidence(List<Observation> observations, List<Recommendation> recommendations) {
        List<Double> scores = new ArrayList<>();
        observations.forEach(observation -> scores.add(observation.confidence()));
        recommendations.forEach(recommendation -> scores.add(recommendation.confidence()));
        if (scores.isEmpty()) {
            return 0;
        }
        return scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private static String extractJson(String raw) {
        String trimmed = raw == null ? "" : raw.trim();
        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf('{');
            int end = trimmed.lastIndexOf('}');
            if (start >= 0 && end > start) {
                return trimmed.substring(start, end + 1);
            }
        }
        return trimmed;
    }

    public record ParsedAnalysis(
            List<Observation> observations, List<Recommendation> recommendations, double confidence, String rawJson) {}
}
