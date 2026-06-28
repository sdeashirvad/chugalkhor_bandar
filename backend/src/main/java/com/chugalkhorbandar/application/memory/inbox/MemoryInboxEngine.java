package com.chugalkhorbandar.application.memory.inbox;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import com.chugalkhorbandar.application.cognition.Observation;
import com.chugalkhorbandar.application.cognition.ObservationType;
import com.chugalkhorbandar.application.cognition.Recommendation;
import com.chugalkhorbandar.application.cognition.RecommendationAction;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MemoryInboxEngine {

    private final MemoryInboxProperties properties;

    public MemoryInboxEngine(MemoryInboxProperties properties) {
        this.properties = properties;
    }

    public MemoryInboxGenerationSnapshot generate(MemoryInboxEngineInput input) {
        List<MemoryInboxGenerationTraceEntry> trace = new ArrayList<>();
        List<MemoryInboxItem> generated = new ArrayList<>();

        for (ConversationArtifact artifact : input.artifacts()) {
            evaluateArtifact(artifact, input, trace, generated);
        }

        if (input.analysis() != null) {
            for (Observation observation : input.analysis().observations()) {
                evaluateObservation(observation, input.analysis(), input, trace, generated);
            }
            for (Recommendation recommendation : input.analysis().recommendations()) {
                evaluateRecommendation(recommendation, input.analysis(), input, trace, generated);
            }
        } else {
            trace.add(new MemoryInboxGenerationTraceEntry(
                    "cognitive-analysis", "No cognitive analysis available for observation/recommendation rules"));
        }

        if (generated.isEmpty()) {
            trace.add(new MemoryInboxGenerationTraceEntry("no-inbox-item", "No inbox rules matched; producing nothing"));
        }

        List<MemoryInboxItem> ordered = generated.stream()
                .sorted(Comparator.comparingInt(MemoryInboxEngine::importanceRank).reversed()
                        .thenComparing(MemoryInboxItem::confidence, Comparator.reverseOrder()))
                .toList();
        return new MemoryInboxGenerationSnapshot(
                input.ownerCharacterId(), input.conversationId(), input.currentTime(), trace, ordered);
    }

    private void evaluateArtifact(
            ConversationArtifact artifact,
            MemoryInboxEngineInput input,
            List<MemoryInboxGenerationTraceEntry> trace,
            List<MemoryInboxItem> generated) {
        if (artifact.type() == ConversationArtifactType.PROMISE) {
            trace.add(new MemoryInboxGenerationTraceEntry("promise-artifact-rule", "PROMISE artifact detected"));
            generated.add(buildItem(
                    artifact.type().name(),
                    MemoryInboxSource.CONVERSATION_ARTIFACT,
                    artifact.id(),
                    input.ownerCharacterId(),
                    artifact.summary(),
                    MemoryInboxImportance.HIGH,
                    confidenceFromPriority(artifact.priority().name()),
                    "",
                    List.of(artifact.id()),
                    input,
                    "promise-artifact"));
        } else {
            trace.add(new MemoryInboxGenerationTraceEntry(
                    "promise-artifact-rule", "Artifact type " + artifact.type() + " not ingested"));
        }
    }

    private void evaluateObservation(
            Observation observation,
            CognitiveAnalysisResult analysis,
            MemoryInboxEngineInput input,
            List<MemoryInboxGenerationTraceEntry> trace,
            List<MemoryInboxItem> generated) {
        if (observation.type() == ObservationType.UNKNOWN) {
            trace.add(new MemoryInboxGenerationTraceEntry(
                    "unknown-observation-rule", "UNKNOWN observation ignored"));
            return;
        }
        if (observation.type() == ObservationType.PREFERENCE) {
            if (observation.confidence() < properties.getMinimumConfidence()) {
                trace.add(new MemoryInboxGenerationTraceEntry(
                        "preference-observation-rule",
                        "PREFERENCE confidence below threshold (" + properties.getMinimumConfidence() + ")"));
                return;
            }
            trace.add(new MemoryInboxGenerationTraceEntry(
                    "preference-observation-rule", "PREFERENCE observation above confidence threshold"));
            generated.add(buildItem(
                    observation.type().name(),
                    MemoryInboxSource.COGNITIVE_OBSERVATION,
                    observation.id(),
                    input.ownerCharacterId(),
                    observation.summary(),
                    MemoryInboxImportance.MEDIUM,
                    observation.confidence(),
                    analysis.analysisId(),
                    List.of(),
                    input,
                    "preference-observation"));
            return;
        }
        if (observation.type() == ObservationType.STORY_SEED) {
            trace.add(new MemoryInboxGenerationTraceEntry(
                    "story-seed-observation-rule", "STORY_SEED observation ingested"));
            generated.add(buildItem(
                    observation.type().name(),
                    MemoryInboxSource.COGNITIVE_OBSERVATION,
                    observation.id(),
                    input.ownerCharacterId(),
                    observation.summary(),
                    MemoryInboxImportance.HIGH,
                    observation.confidence(),
                    analysis.analysisId(),
                    List.of(),
                    input,
                    "story-seed-observation"));
            return;
        }
        trace.add(new MemoryInboxGenerationTraceEntry(
                "observation-rule", "Observation type " + observation.type() + " not ingested"));
    }

    private void evaluateRecommendation(
            Recommendation recommendation,
            CognitiveAnalysisResult analysis,
            MemoryInboxEngineInput input,
            List<MemoryInboxGenerationTraceEntry> trace,
            List<MemoryInboxItem> generated) {
        if (recommendation.action() == RecommendationAction.PROMOTE_TO_MEMORY) {
            trace.add(new MemoryInboxGenerationTraceEntry(
                    "promote-recommendation-rule", "PROMOTE_TO_MEMORY recommendation ingested"));
            generated.add(buildItem(
                    recommendation.action().name(),
                    MemoryInboxSource.COGNITIVE_RECOMMENDATION,
                    recommendation.id(),
                    input.ownerCharacterId(),
                    recommendation.reason(),
                    MemoryInboxImportance.VERY_HIGH,
                    recommendation.confidence(),
                    analysis.analysisId(),
                    List.of(),
                    input,
                    "promote-recommendation"));
        } else {
            trace.add(new MemoryInboxGenerationTraceEntry(
                    "promote-recommendation-rule",
                    "Recommendation action " + recommendation.action() + " not ingested"));
        }
    }

    private MemoryInboxItem buildItem(
            String type,
            MemoryInboxSource source,
            String sourceId,
            String ownerCharacterId,
            String summary,
            MemoryInboxImportance importance,
            double confidence,
            String analysisId,
            List<String> artifactIds,
            MemoryInboxEngineInput input,
            String trigger) {
        Instant expiresAt = input.currentTime().plus(properties.getDefaultExpirationDays(), ChronoUnit.DAYS);
        return new MemoryInboxItem(
                UUID.randomUUID().toString(),
                type,
                source,
                sourceId,
                ownerCharacterId,
                summary,
                importance,
                confidence,
                MemoryInboxStatus.NEW,
                input.currentTime(),
                expiresAt,
                Map.of(
                        "trigger", trigger,
                        "conversationId", input.conversationId(),
                        "evidence", summary),
                List.of("created:" + trigger),
                analysisId,
                artifactIds);
    }

    private static double confidenceFromPriority(String priority) {
        return switch (priority) {
            case "CRITICAL" -> 0.95;
            case "HIGH" -> 0.85;
            case "MEDIUM" -> 0.75;
            default -> 0.65;
        };
    }

    private static int importanceRank(MemoryInboxItem item) {
        return switch (item.importance()) {
            case VERY_HIGH -> 4;
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }
}
