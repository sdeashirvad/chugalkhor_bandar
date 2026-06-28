package com.chugalkhorbandar.application.memory.inbox;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactEngine;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactPriority;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import com.chugalkhorbandar.application.cognition.Observation;
import com.chugalkhorbandar.application.cognition.ObservationType;
import com.chugalkhorbandar.application.cognition.Recommendation;
import com.chugalkhorbandar.application.cognition.RecommendationAction;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemoryInboxEngineTest {

    private MemoryInboxEngine engine;
    private Instant now;

    @BeforeEach
    void setUp() {
        MemoryInboxProperties properties = new MemoryInboxProperties();
        properties.setMinimumConfidence(0.5);
        properties.setDefaultExpirationDays(30);
        engine = new MemoryInboxEngine(properties);
        now = Instant.parse("2026-06-01T12:00:00Z");
    }

    @Test
    void ingestsPromoteToMemoryRecommendation() {
        MemoryInboxGenerationSnapshot snapshot = engine.generate(input(
                List.of(),
                analysis(
                        List.of(),
                        List.of(new Recommendation(
                                "rec-1",
                                RecommendationAction.PROMOTE_TO_MEMORY,
                                0.9,
                                "Worth keeping",
                                "memory",
                                Map.of())))));

        assertThat(snapshot.generatedItems()).hasSize(1);
        MemoryInboxItem item = snapshot.generatedItems().getFirst();
        assertThat(item.source()).isEqualTo(MemoryInboxSource.COGNITIVE_RECOMMENDATION);
        assertThat(item.importance()).isEqualTo(MemoryInboxImportance.VERY_HIGH);
        assertThat(item.summary()).isEqualTo("Worth keeping");
        assertThat(snapshot.trace()).extracting(MemoryInboxGenerationTraceEntry::rule).contains("promote-recommendation-rule");
    }

    @Test
    void ingestsPreferenceObservationAboveThreshold() {
        MemoryInboxGenerationSnapshot snapshot = engine.generate(input(
                List.of(),
                analysis(
                        List.of(new Observation(
                                "obs-1",
                                ObservationType.PREFERENCE,
                                0.8,
                                "Likes mangoes",
                                "User said they love mangoes",
                                Map.of(),
                                now)),
                        List.of())));

        assertThat(snapshot.generatedItems()).hasSize(1);
        assertThat(snapshot.generatedItems().getFirst().source()).isEqualTo(MemoryInboxSource.COGNITIVE_OBSERVATION);
        assertThat(snapshot.generatedItems().getFirst().importance()).isEqualTo(MemoryInboxImportance.MEDIUM);
    }

    @Test
    void skipsPreferenceObservationBelowThreshold() {
        MemoryInboxGenerationSnapshot snapshot = engine.generate(input(
                List.of(),
                analysis(
                        List.of(new Observation(
                                "obs-1",
                                ObservationType.PREFERENCE,
                                0.3,
                                "Maybe likes mangoes",
                                "Weak signal",
                                Map.of(),
                                now)),
                        List.of())));

        assertThat(snapshot.generatedItems()).isEmpty();
        assertThat(snapshot.trace()).extracting(MemoryInboxGenerationTraceEntry::rule).contains("preference-observation-rule");
    }

    @Test
    void ingestsStorySeedObservation() {
        MemoryInboxGenerationSnapshot snapshot = engine.generate(input(
                List.of(),
                analysis(
                        List.of(new Observation(
                                "obs-1",
                                ObservationType.STORY_SEED,
                                0.6,
                                "Jungle adventure seed",
                                "User asked for a story",
                                Map.of(),
                                now)),
                        List.of())));

        assertThat(snapshot.generatedItems()).hasSize(1);
        assertThat(snapshot.generatedItems().getFirst().importance()).isEqualTo(MemoryInboxImportance.HIGH);
    }

    @Test
    void ingestsPromiseArtifact() {
        MemoryInboxGenerationSnapshot snapshot = engine.generate(input(
                List.of(promiseArtifact("art-1", "Remember this promise")),
                null));

        assertThat(snapshot.generatedItems()).hasSize(1);
        MemoryInboxItem item = snapshot.generatedItems().getFirst();
        assertThat(item.source()).isEqualTo(MemoryInboxSource.CONVERSATION_ARTIFACT);
        assertThat(item.artifactIds()).containsExactly("art-1");
        assertThat(item.importance()).isEqualTo(MemoryInboxImportance.HIGH);
    }

    @Test
    void ignoresUnknownObservation() {
        MemoryInboxGenerationSnapshot snapshot = engine.generate(input(
                List.of(),
                analysis(
                        List.of(new Observation(
                                "obs-1",
                                ObservationType.UNKNOWN,
                                0.9,
                                "Unclear",
                                "No evidence",
                                Map.of(),
                                now)),
                        List.of())));

        assertThat(snapshot.generatedItems()).isEmpty();
        assertThat(snapshot.trace()).extracting(MemoryInboxGenerationTraceEntry::rule).contains("unknown-observation-rule");
    }

    @Test
    void producesNothingWhenNoRulesMatch() {
        MemoryInboxGenerationSnapshot snapshot = engine.generate(input(List.of(), null));

        assertThat(snapshot.generatedItems()).isEmpty();
        assertThat(snapshot.trace()).extracting(MemoryInboxGenerationTraceEntry::rule).contains("no-inbox-item");
    }

    private MemoryInboxEngineInput input(List<ConversationArtifact> artifacts, CognitiveAnalysisResult analysis) {
        return new MemoryInboxEngineInput(
                "character_alpha",
                "conv-1",
                artifacts,
                analysis,
                "status=READY",
                now,
                List.of());
    }

    private CognitiveAnalysisResult analysis(List<Observation> observations, List<Recommendation> recommendations) {
        return new CognitiveAnalysisResult(
                "analysis-1",
                "character_alpha",
                "conv-1",
                "mock",
                "mock-model",
                10,
                0.8,
                now,
                observations,
                recommendations,
                "{}");
    }

    private ConversationArtifact promiseArtifact(String id, String summary) {
        return new ConversationArtifact(
                id,
                ConversationArtifactType.PROMISE,
                ConversationArtifactEngine.BANDAR_CHARACTER_ID,
                "character_alpha",
                "character_alpha",
                "conv-1",
                "Promise",
                summary,
                ConversationArtifactStatus.ACTIVE,
                ConversationArtifactPriority.HIGH,
                now,
                now,
                now.plusSeconds(86400 * 30),
                Map.of(),
                List.of("created:promise-made"));
    }
}
