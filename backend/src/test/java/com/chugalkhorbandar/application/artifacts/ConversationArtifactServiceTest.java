package com.chugalkhorbandar.application.artifacts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryConversationArtifactRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryConversationArtifactStore;
import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisProperties;
import com.chugalkhorbandar.application.conversation.director.ConversationDirectorService;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxService;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
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
class ConversationArtifactServiceTest {

    @Mock
    private com.chugalkhorbandar.application.session.SessionService sessionService;

    @Mock
    private WorkingMemoryService workingMemoryService;

    @Mock
    private BehaviorEngineService behaviorEngineService;

    @Mock
    private ConversationDirectorService conversationDirectorService;

    @Mock
    private WorldStatusQueryService worldStatusQueryService;

    @Mock
    private MemoryInboxService memoryInboxService;

    private InMemoryConversationArtifactRepository repository;
    private ConversationArtifactProperties properties;
    private ConversationArtifactGenerationStore generationStore;
    private ConversationArtifactEngine engine;
    private ConversationArtifactService artifactService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryConversationArtifactRepository(new InMemoryConversationArtifactStore());
        properties = new ConversationArtifactProperties();
        generationStore = new ConversationArtifactGenerationStore();
        engine = new ConversationArtifactEngine(properties);
        artifactService = new ConversationArtifactService(
                sessionService,
                engine,
                repository,
                properties,
                workingMemoryService,
                behaviorEngineService,
                conversationDirectorService,
                worldStatusQueryService,
                generationStore,
                new CognitiveAnalysisProperties(),
                memoryInboxService);
    }

    @Test
    void fulfillAndCancelLifecycle() {
        ChatSession session = session("session-1", "character_alpha");
        when(sessionService.requireSession("session-1")).thenReturn(session);
        ConversationArtifact artifact = repository.save(activeArtifact("a-1", "character_alpha"));

        ConversationArtifact fulfilled = artifactService.fulfill("session-1", artifact.id());
        assertThat(fulfilled.status()).isEqualTo(ConversationArtifactStatus.FULFILLED);
        assertThat(fulfilled.trace()).contains("fulfilled");

        repository.save(activeArtifact("a-2", "character_alpha"));
        ConversationArtifact cancelled = artifactService.cancel("session-1", "a-2");
        assertThat(cancelled.status()).isEqualTo(ConversationArtifactStatus.ARCHIVED);
        assertThat(cancelled.trace()).contains("cancelled", "archived");
    }

    @Test
    void expiresActiveArtifacts() {
        ChatSession session = session("session-1", "character_alpha");
        when(sessionService.requireSession("session-1")).thenReturn(session);
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        repository.save(new ConversationArtifact(
                "expired-1",
                ConversationArtifactType.PROMISE,
                ConversationArtifactEngine.BANDAR_CHARACTER_ID,
                "character_alpha",
                "character_alpha",
                "conv-1",
                "Promise",
                "summary",
                ConversationArtifactStatus.ACTIVE,
                ConversationArtifactPriority.HIGH,
                now.minus(40, ChronoUnit.DAYS),
                now.minus(40, ChronoUnit.DAYS),
                now.minus(1, ChronoUnit.DAYS),
                Map.of(),
                List.of("created:promise-made", "activated")));

        List<ConversationArtifact> listed = artifactService.listForSession("session-1");
        assertThat(listed).extracting(ConversationArtifact::status).contains(ConversationArtifactStatus.ARCHIVED);
    }

    @Test
    void listsOnlyRelevantArtifacts() {
        ChatSession session = session("session-1", "character_alpha");
        when(sessionService.requireSession("session-1")).thenReturn(session);
        repository.save(activeArtifact("owned", "character_alpha"));
        repository.save(activeArtifact("other", "character_beta"));

        assertThat(artifactService.listForSession("session-1")).extracting(ConversationArtifact::id).containsExactly("owned");
    }

    private static ConversationArtifact activeArtifact(String id, String userCharacterId) {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        return new ConversationArtifact(
                id,
                ConversationArtifactType.PROMISE,
                ConversationArtifactEngine.BANDAR_CHARACTER_ID,
                userCharacterId,
                userCharacterId,
                "conv-1",
                "Promise",
                "summary",
                ConversationArtifactStatus.ACTIVE,
                ConversationArtifactPriority.HIGH,
                now,
                now,
                now.plus(30, ChronoUnit.DAYS),
                Map.of(),
                List.of("created:promise-made", "activated"));
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
