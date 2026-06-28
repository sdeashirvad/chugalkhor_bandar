package com.chugalkhorbandar.application.cognition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryCognitiveAnalysisRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryCognitiveAnalysisStore;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactService;
import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.application.conversation.director.ConversationDirectorService;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.query.WorldStatus;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import com.chugalkhorbandar.domain.conversation.ports.ConversationMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CognitiveAnalysisServiceTest {

    @Mock
    private com.chugalkhorbandar.application.session.SessionService sessionService;

    @Mock
    private WorkingMemoryService workingMemoryService;

    @Mock
    private BehaviorEngineService behaviorEngineService;

    @Mock
    private ConversationDirectorService conversationDirectorService;

    @Mock
    private ConversationArtifactService conversationArtifactService;

    @Mock
    private ConversationMessageRepository messages;

    @Mock
    private WorldStatusQueryService worldStatusQueryService;

    @Mock
    private com.chugalkhorbandar.application.memory.inbox.MemoryInboxService memoryInboxService;

    private CognitiveAnalysisService service;

    @BeforeEach
    void setUp() {
        CognitiveAnalysisProperties properties = new CognitiveAnalysisProperties();
        properties.setMockEnabled(true);
        properties.setProvider("mock");
        MockCognitiveAnalysisProvider mockProvider = new MockCognitiveAnalysisProvider(new ObjectMapper());
        CognitiveAnalysisProviderRegistry registry =
                new CognitiveAnalysisProviderRegistry(properties, mockProvider, null);
        InMemoryCognitiveAnalysisRepository repository =
                new InMemoryCognitiveAnalysisRepository(new InMemoryCognitiveAnalysisStore());
        service = new CognitiveAnalysisService(
                sessionService,
                new CognitiveAnalysisEngine(registry, new CognitiveAnalysisJsonParser(new ObjectMapper()), properties),
                repository,
                new CognitiveAnalysisGenerationStore(),
                properties,
                registry,
                workingMemoryService,
                behaviorEngineService,
                conversationDirectorService,
                conversationArtifactService,
                messages,
                worldStatusQueryService,
                memoryInboxService);
    }

    @Test
    void persistsAnalysisResult() {
        ChatSession session = session("session-1", "character_alpha");
        when(sessionService.requireSession("session-1")).thenReturn(session);
        when(messages.findByConversationIdOrdered("conv-1"))
                .thenReturn(List.of(new ConversationMessage(
                        "m-1",
                        "conv-1",
                        Sender.USER,
                        Instant.parse("2026-06-01T12:00:00Z"),
                        "tell me a story",
                        Visibility.PUBLIC,
                        Map.of())));
        when(conversationArtifactService.listForSession("session-1")).thenReturn(List.of());
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

        Conversation conversation = conversation("conv-1");
        service.analyzeCompletedTurn("session-1", conversation, "tell me a story");

        assertThat(service.getLatestForSession("session-1")).isPresent();
        assertThat(service.getLatestForSession("session-1").orElseThrow().observations()).isNotEmpty();
    }

    @Test
    void recordsFailureWithoutThrowing() {
        CognitiveAnalysisProperties properties = new CognitiveAnalysisProperties();
        properties.setMockEnabled(false);
        properties.setProvider("groq");
        MockCognitiveAnalysisProvider mockProvider = new MockCognitiveAnalysisProvider(new ObjectMapper());
        GroqCognitiveAnalysisProvider groqProvider = org.mockito.Mockito.mock(GroqCognitiveAnalysisProvider.class);
        when(groqProvider.providerName()).thenReturn("groq");
        when(groqProvider.isAvailable()).thenReturn(true);
        when(groqProvider.analyzeConversation(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new RuntimeException("provider down"));
        CognitiveAnalysisProviderRegistry registry =
                new CognitiveAnalysisProviderRegistry(properties, mockProvider, groqProvider);
        InMemoryCognitiveAnalysisRepository repository =
                new InMemoryCognitiveAnalysisRepository(new InMemoryCognitiveAnalysisStore());
        CognitiveAnalysisGenerationStore generationStore = new CognitiveAnalysisGenerationStore();
        CognitiveAnalysisService failingService = new CognitiveAnalysisService(
                sessionService,
                new CognitiveAnalysisEngine(registry, new CognitiveAnalysisJsonParser(new ObjectMapper()), properties),
                repository,
                generationStore,
                properties,
                registry,
                workingMemoryService,
                behaviorEngineService,
                conversationDirectorService,
                conversationArtifactService,
                messages,
                worldStatusQueryService,
                memoryInboxService);

        ChatSession session = session("session-1", "character_alpha");
        when(sessionService.requireSession("session-1")).thenReturn(session);
        when(messages.findByConversationIdOrdered("conv-1")).thenReturn(List.of());
        when(conversationArtifactService.listForSession("session-1")).thenReturn(List.of());
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

        failingService.analyzeCompletedTurn("session-1", conversation("conv-1"), "hello");

        assertThat(generationStore.findByCharacterId("character_alpha")).isPresent();
        assertThat(generationStore.findByCharacterId("character_alpha").orElseThrow().success()).isFalse();
    }

    private static ChatSession session(String sessionId, String characterId) {
        return new ChatSession(
                sessionId,
                new CurrentCharacter(characterId, "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                Instant.parse("2026-06-01T12:00:00Z"),
                Instant.parse("2026-06-01T12:00:00Z"),
                SessionStatus.ACTIVE);
    }

    private static Conversation conversation(String conversationId) {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        return new Conversation(
                conversationId,
                "session-1",
                new ConversationCharacter("character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null),
                now,
                now,
                ConversationStatus.ACTIVE,
                List.of());
    }
}
