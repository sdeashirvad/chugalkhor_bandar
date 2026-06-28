package com.chugalkhorbandar.application.memory.inbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryMemoryInboxRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryMemoryInboxStore;
import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactEngine;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactPriority;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactService;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import com.chugalkhorbandar.application.cognition.Observation;
import com.chugalkhorbandar.application.cognition.ObservationType;
import com.chugalkhorbandar.application.cognition.Recommendation;
import com.chugalkhorbandar.application.cognition.RecommendationAction;
import com.chugalkhorbandar.application.query.WorldStatus;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemoryInboxServiceTest {

    @Mock
    private com.chugalkhorbandar.application.session.SessionService sessionService;

    @Mock
    private ConversationArtifactService conversationArtifactService;

    @Mock
    private WorldStatusQueryService worldStatusQueryService;

    private InMemoryMemoryInboxRepository repository;
    private MemoryInboxProperties properties;
    private MemoryInboxService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMemoryInboxRepository(new InMemoryMemoryInboxStore());
        properties = new MemoryInboxProperties();
        MemoryInboxEngine engine = new MemoryInboxEngine(properties);
        service = new MemoryInboxService(
                sessionService,
                engine,
                repository,
                properties,
                new MemoryInboxGenerationStore(),
                conversationArtifactService,
                worldStatusQueryService);
    }

    @Test
    void ingestsAndPersistsItems() {
        stubSessionAndWorld();
        when(conversationArtifactService.listForSession("session-1"))
                .thenReturn(List.of(promiseArtifact("art-1", "conv-1")));

        service.ingestForCompletedTurn("session-1", "conv-1", analysisWithRecommendation());

        List<MemoryInboxItem> items = service.listForSession("session-1");
        assertThat(items).isNotEmpty();
        assertThat(items).anyMatch(item -> item.source() == MemoryInboxSource.CONVERSATION_ARTIFACT);
        assertThat(items).anyMatch(item -> item.source() == MemoryInboxSource.COGNITIVE_RECOMMENDATION);
    }

    @Test
    void deduplicatesSameSource() {
        stubSessionAndWorld();
        when(conversationArtifactService.listForSession("session-1"))
                .thenReturn(List.of(promiseArtifact("art-1", "conv-1")));

        service.ingestForCompletedTurn("session-1", "conv-1", null);
        service.ingestForCompletedTurn("session-1", "conv-1", null);

        long promiseItems = service.listForSession("session-1").stream()
                .filter(item -> item.source() == MemoryInboxSource.CONVERSATION_ARTIFACT)
                .count();
        assertThat(promiseItems).isEqualTo(1);
    }

    @Test
    void skipsArtifactAlreadyPromoted() {
        stubSessionAndWorld();
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        repository.save(new MemoryInboxItem(
                "existing-1",
                "PROMISE",
                MemoryInboxSource.CONVERSATION_ARTIFACT,
                "art-1",
                "character_alpha",
                "Already promoted",
                MemoryInboxImportance.HIGH,
                0.85,
                MemoryInboxStatus.PROMOTED,
                now,
                now.plus(30, ChronoUnit.DAYS),
                Map.of(),
                List.of(),
                "",
                List.of("art-1")));
        when(conversationArtifactService.listForSession("session-1"))
                .thenReturn(List.of(promiseArtifact("art-1", "conv-1")));

        service.ingestForCompletedTurn("session-1", "conv-1", null);

        assertThat(service.listForSession("session-1")).hasSize(1);
    }

    @Test
    void mergesIdenticalObservations() {
        stubSessionAndWorld();
        when(conversationArtifactService.listForSession("session-1")).thenReturn(List.of());
        CognitiveAnalysisResult first = analysisWithObservation("obs-1", "Likes mangoes");
        CognitiveAnalysisResult second = analysisWithObservation("obs-2", "Likes mangoes");

        service.ingestForCompletedTurn("session-1", "conv-1", first);
        service.ingestForCompletedTurn("session-1", "conv-1", second);

        List<MemoryInboxItem> observations = service.listForSession("session-1").stream()
                .filter(item -> item.source() == MemoryInboxSource.COGNITIVE_OBSERVATION)
                .toList();
        assertThat(observations).hasSize(1);
        assertThat(observations.getFirst().trace()).contains("merged-duplicate-observation");
    }

    @Test
    void reviewDiscardAndExpireLifecycle() {
        when(sessionService.requireSession("session-1")).thenReturn(session("session-1", "character_alpha"));
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        MemoryInboxItem fresh = repository.save(new MemoryInboxItem(
                "item-1",
                "PREFERENCE",
                MemoryInboxSource.COGNITIVE_OBSERVATION,
                "obs-1",
                "character_alpha",
                "Likes tea",
                MemoryInboxImportance.MEDIUM,
                0.8,
                MemoryInboxStatus.NEW,
                now,
                now.plus(30, ChronoUnit.DAYS),
                Map.of("evidence", "User prefers tea"),
                List.of("created:preference-observation"),
                "analysis-1",
                List.of()));

        MemoryInboxItem reviewed = service.review("session-1", fresh.id());
        assertThat(reviewed.status()).isEqualTo(MemoryInboxStatus.REVIEWED);

        MemoryInboxItem discarded = service.discard("session-1", fresh.id());
        assertThat(discarded.status()).isEqualTo(MemoryInboxStatus.ARCHIVED);
        assertThat(discarded.trace()).contains("discarded", "archived");

        repository.save(new MemoryInboxItem(
                "item-2",
                "STORY_SEED",
                MemoryInboxSource.COGNITIVE_OBSERVATION,
                "obs-2",
                "character_alpha",
                "Story idea",
                MemoryInboxImportance.HIGH,
                0.7,
                MemoryInboxStatus.NEW,
                now.minus(40, ChronoUnit.DAYS),
                now.minus(1, ChronoUnit.DAYS),
                Map.of(),
                List.of("created:story-seed-observation"),
                "analysis-2",
                List.of()));

        List<MemoryInboxItem> listed = service.listForSession("session-1");
        assertThat(listed.stream().filter(item -> item.id().equals("item-2")).findFirst().orElseThrow().status())
                .isEqualTo(MemoryInboxStatus.ARCHIVED);
    }

    @Test
    void persistsAcrossRepositoryReload() {
        stubSessionAndWorld();
        when(conversationArtifactService.listForSession("session-1"))
                .thenReturn(List.of(promiseArtifact("art-1", "conv-1")));

        service.ingestForCompletedTurn("session-1", "conv-1", null);
        int countBefore = service.listForSession("session-1").size();

        InMemoryMemoryInboxStore sharedStore = new InMemoryMemoryInboxStore();
        repository.findByOwnerCharacterId("character_alpha").forEach(sharedStore::save);
        MemoryInboxService reloadedService = new MemoryInboxService(
                sessionService,
                new MemoryInboxEngine(properties),
                new InMemoryMemoryInboxRepository(sharedStore),
                properties,
                new MemoryInboxGenerationStore(),
                conversationArtifactService,
                worldStatusQueryService);

        assertThat(reloadedService.listForSession("session-1")).hasSize(countBefore);
    }

    @Test
    void recordsGenerationTrace() {
        stubSessionAndWorld();
        when(conversationArtifactService.listForSession("session-1"))
                .thenReturn(List.of(promiseArtifact("art-1", "conv-1")));

        service.ingestForCompletedTurn("session-1", "conv-1", null);

        assertThat(service.getLatestGeneration("session-1")).isPresent();
        assertThat(service.getLatestGeneration("session-1").orElseThrow().generatedItems()).isNotEmpty();
    }

    private void stubSessionAndWorld() {
        when(sessionService.requireSession("session-1")).thenReturn(session("session-1", "character_alpha"));
        when(worldStatusQueryService.getStatus())
                .thenReturn(new WorldStatus(
                        "READY",
                        "1.0",
                        Instant.parse("2026-06-01T00:00:00Z"),
                        Instant.parse("2026-06-01T12:00:00Z"),
                        "IN_MEMORY",
                        1,
                        1,
                        0,
                        0,
                        0,
                        0,
                        0,
                        Map.of(),
                        Map.of()));
    }

    private static CognitiveAnalysisResult analysisWithRecommendation() {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        return new CognitiveAnalysisResult(
                "analysis-1",
                "character_alpha",
                "conv-1",
                "mock",
                "mock",
                5,
                0.9,
                now,
                List.of(),
                List.of(new Recommendation(
                        "rec-1",
                        RecommendationAction.PROMOTE_TO_MEMORY,
                        0.95,
                        "Keep this",
                        "memory",
                        Map.of())),
                "{}");
    }

    private static CognitiveAnalysisResult analysisWithObservation(String id, String summary) {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        return new CognitiveAnalysisResult(
                "analysis-" + id,
                "character_alpha",
                "conv-1",
                "mock",
                "mock",
                5,
                0.8,
                now,
                List.of(new Observation(
                        id,
                        ObservationType.PREFERENCE,
                        0.8,
                        summary,
                        "Evidence for " + summary,
                        Map.of(),
                        now)),
                List.of(),
                "{}");
    }

    private static ConversationArtifact promiseArtifact(String id, String conversationId) {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        return new ConversationArtifact(
                id,
                ConversationArtifactType.PROMISE,
                ConversationArtifactEngine.BANDAR_CHARACTER_ID,
                "character_alpha",
                "character_alpha",
                conversationId,
                "Promise",
                "Remember this",
                ConversationArtifactStatus.ACTIVE,
                ConversationArtifactPriority.HIGH,
                now,
                now,
                now.plus(30, ChronoUnit.DAYS),
                Map.of(),
                List.of("created:promise-made"));
    }

    private static ChatSession session(String sessionId, String characterId) {
        return new ChatSession(
                sessionId,
                new CurrentCharacter(characterId, "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                Instant.parse("2026-06-01T12:00:00Z"),
                Instant.parse("2026-06-01T12:00:00Z"),
                SessionStatus.ACTIVE);
    }
}
