package com.chugalkhorbandar.application.memory.working;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryCharacterCredentialRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryConversationMessageRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryConversationRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryConversationStore;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorkingMemoryRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorkingMemoryStore;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextRequestFactory;
import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.context.knowledge.provider.ConversationKnowledgeProvider;
import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.application.conversation.ConversationService;
import com.chugalkhorbandar.application.conversation.ConversationWindowBuilder;
import com.chugalkhorbandar.application.conversation.director.ConversationArc;
import com.chugalkhorbandar.application.conversation.director.ConversationDirectorService;
import com.chugalkhorbandar.application.conversation.director.ConversationEnergy;
import com.chugalkhorbandar.application.conversation.director.ConversationGoal;
import com.chugalkhorbandar.application.conversation.director.ConversationOutcome;
import com.chugalkhorbandar.application.conversation.director.ConversationPlan;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanExecutor;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanningTrace;
import com.chugalkhorbandar.application.llm.LLMGenerateResult;
import com.chugalkhorbandar.application.llm.LLMProviderInfo;
import com.chugalkhorbandar.application.llm.LLMProviderType;
import com.chugalkhorbandar.application.llm.LLMService;
import com.chugalkhorbandar.application.llm.ProviderResponse;
import com.chugalkhorbandar.application.llm.ProviderTokenUsage;
import com.chugalkhorbandar.application.notification.NotificationService;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactService;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisTrigger;
import com.chugalkhorbandar.application.query.EntityReferenceResolver;
import com.chugalkhorbandar.application.session.InMemorySessionStore;
import com.chugalkhorbandar.application.session.SessionService;
import com.chugalkhorbandar.config.ChugalkhorProperties;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.ConversationWindow;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkingMemoryIntegrationTest {

    private final InMemoryWorldStore worldStore = new InMemoryWorldStore();
    private final InMemoryCharacterCredentialRepository credentials = new InMemoryCharacterCredentialRepository();
    private final InMemoryConversationStore conversationStore = new InMemoryConversationStore();
    private final InMemoryWorkingMemoryStore workingMemoryStore = new InMemoryWorkingMemoryStore();
    private final WorkingMemoryProperties properties = new WorkingMemoryProperties();
    private final ChugalkhorProperties chugalkhorProperties = new ChugalkhorProperties();

    @Mock
    private LLMService llmService;

    @Mock
    private ContextRequestFactory contextRequestFactory;

    @Mock
    private ConversationDirectorService conversationDirectorService;

    @Mock
    private ConversationPlanExecutor conversationPlanExecutor;

    @Mock
    private BehaviorEngineService behaviorEngineService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ConversationArtifactService conversationArtifactService;

    @Mock
    private CognitiveAnalysisTrigger cognitiveAnalysisTrigger;

    private InMemorySessionStore sessionStore;
    private SessionService sessionService;
    private WorkingMemoryService workingMemoryService;
    private ConversationService conversationService;

    @BeforeEach
    void setUp() {
        WorkingMemoryService[] workingMemoryHolder = new WorkingMemoryService[1];
        sessionStore = new InMemorySessionStore(List.of(sessionId -> {
            if (workingMemoryHolder[0] != null) {
                workingMemoryHolder[0].delete(sessionId);
            }
        }));
        sessionService = new SessionService(
                new InMemoryWorldRepositoryProvider(worldStore),
                credentials,
                new EntityReferenceResolver(new InMemoryWorldRepositoryProvider(worldStore)),
                sessionStore,
                chugalkhorProperties,
                notificationService,
                new com.chugalkhorbandar.application.session.CharacterPresenceStore());
        WorkingMemoryBuilder builder = new WorkingMemoryBuilder(properties);
        workingMemoryService = new WorkingMemoryService(
                sessionService, contextRequestFactory, builder, new InMemoryWorkingMemoryRepository(workingMemoryStore));
        workingMemoryHolder[0] = workingMemoryService;
        conversationService = new ConversationService(
                sessionService,
                new InMemoryConversationRepository(conversationStore),
                new InMemoryConversationMessageRepository(conversationStore),
                llmService,
                workingMemoryService,
                conversationDirectorService,
                behaviorEngineService,
                conversationPlanExecutor,
                conversationArtifactService,
                cognitiveAnalysisTrigger);

        worldStore.characters()
                .put(
                        "character_alpha",
                        new RuntimeCharacter(
                                "character_alpha",
                                "Alpha",
                                Map.of("titles", "- Alpha\n", "basicInformation", "| Species | Rabbitu |"),
                                null,
                                Map.of()));
        credentials.save("character_alpha", "secret");
        sessionStore.setInactivityTimeout(Duration.ofMinutes(30));
    }

    @Test
    void conversationProviderTrimsToConfiguredWindow() {
        properties.setConversationWindowMessages(10);
        ConversationKnowledgeProvider provider = new ConversationKnowledgeProvider(properties);
        Conversation conversation = longConversation(15);
        String content = provider.resolve(
                        provider.plan(plannerRequest(conversation), java.util.Set.of(com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType.CONVERSATION))
                                .get(0),
                        plannerRequest(conversation))
                .orElseThrow()
                .content();

        ConversationWindow window = ConversationWindowBuilder.build(conversation, 10);
        assertThat(content.split("\n")).hasSize(window.messages().size());
        assertThat(content).contains("message-14");
        assertThat(content).doesNotContain("message-0\n");
    }

    @Test
    void rebuildEndpointPersistsSnapshot() {
        String sessionId = sessionService.login("Alpha", "secret").sessionId();
        Conversation conversation = conversationService.startConversation(sessionId);
        when(contextRequestFactory.create(eq(sessionId), eq("")))
                .thenReturn(plannerRequest(conversation.withMessages(List.of(
                        new ConversationMessage(
                                "m1",
                                conversation.conversationId(),
                                Sender.USER,
                                Instant.now(),
                                "Where am I?",
                                com.chugalkhorbandar.domain.conversation.Visibility.PUBLIC,
                                Map.of())))));

        WorkingMemorySnapshot snapshot = workingMemoryService.rebuild(sessionId);

        assertThat(snapshot.memory().activeTopic()).isEqualTo("Location");
        assertThat(workingMemoryStore.findBySessionId(sessionId)).isPresent();
    }

    @Test
    void cleanupAfterSessionExpiration() {
        String sessionId = sessionService.login("Alpha", "secret").sessionId();
        when(contextRequestFactory.create(eq(sessionId), eq("")))
                .thenReturn(plannerRequest(conversationService.startConversation(sessionId)));
        workingMemoryService.rebuild(sessionId);
        assertThat(workingMemoryStore.findBySessionId(sessionId)).isPresent();

        Instant stale = Instant.now().minus(Duration.ofMinutes(31));
        sessionStore.register(new com.chugalkhorbandar.application.session.ChatSession(
                sessionId,
                new com.chugalkhorbandar.application.session.CurrentCharacter(
                        "character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                stale,
                stale,
                com.chugalkhorbandar.application.session.SessionStatus.ACTIVE));

        assertThat(sessionStore.find(sessionId)).isEmpty();
        assertThat(workingMemoryStore.findBySessionId(sessionId)).isEmpty();
    }

    @Test
    void cleanupAfterConversationClose() {
        String sessionId = sessionService.login("Alpha", "secret").sessionId();
        conversationService.startConversation(sessionId);
        when(contextRequestFactory.create(eq(sessionId), eq("")))
                .thenReturn(plannerRequest(conversationService.currentConversation(sessionId)));
        workingMemoryService.rebuild(sessionId);

        conversationService.closeConversation(sessionId);

        assertThat(workingMemoryStore.findBySessionId(sessionId)).isEmpty();
    }

    @Test
    void appendUserMessageRebuildsWorkingMemoryBeforeLlmCall() {
        String sessionId = sessionService.login("Alpha", "secret").sessionId();
        Conversation conversation = conversationService.startConversation(sessionId);
        ConversationPlanSnapshot planSnapshot = ConversationPlanSnapshot.planned(
                sessionId,
                new ConversationPlan(
                        ConversationGoal.SMALL_TALK,
                        0.65,
                        true,
                        ConversationEnergy.LOW,
                        ConversationArc.SMALL_TALK,
                        1,
                        List.of(),
                        false,
                        false,
                        false,
                        false,
                        false,
                        "Thoughtful",
                        ConversationOutcome.UNRESOLVED,
                        Instant.now(),
                        false,
                        false,
                        null,
                        null),
                new ConversationPlanningTrace(List.of()));
        when(contextRequestFactory.create(eq(sessionId), eq("")))
                .thenReturn(plannerRequest(conversation));
        when(conversationDirectorService.plan(eq(sessionId), eq("Hello"))).thenReturn(planSnapshot);
        when(conversationPlanExecutor.execute(eq(planSnapshot), eq(sessionId), eq("Hello"), any()))
                .thenReturn(List.of(new ConversationMessage(
                        "m1",
                        conversation.conversationId(),
                        Sender.BANDAR,
                        Instant.now(),
                        "Hi",
                        com.chugalkhorbandar.domain.conversation.Visibility.PUBLIC,
                        Map.of())));

        conversationService.appendUserMessage(sessionId, "Hello");

        verify(conversationDirectorService).plan(eq(sessionId), eq("Hello"));
        assertThat(workingMemoryStore.findBySessionId(sessionId)).isPresent();
    }

    private static ContextPlannerRequest plannerRequest(Conversation conversation) {
        return new ContextPlannerRequest(
                new com.chugalkhorbandar.application.session.CurrentCharacter(
                        "character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                new com.chugalkhorbandar.application.session.ChatSession(
                        conversation.sessionId(),
                        new com.chugalkhorbandar.application.session.CurrentCharacter(
                                "character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                        Instant.now(),
                        Instant.now(),
                        com.chugalkhorbandar.application.session.SessionStatus.ACTIVE),
                conversation,
                "",
                new RuntimeWorldContext("READY", "1.0", 1, 0, List.of("Hippu King")));
    }

    private static Conversation longConversation(int messageCount) {
        List<ConversationMessage> messages = new java.util.ArrayList<>();
        for (int index = 0; index < messageCount; index++) {
            messages.add(new ConversationMessage(
                    "message-" + index,
                    "conv-1",
                    index % 2 == 0 ? Sender.USER : Sender.BANDAR,
                    Instant.parse("2026-01-01T00:00:0" + Math.min(index, 9) + "Z"),
                    "message-" + index,
                    com.chugalkhorbandar.domain.conversation.Visibility.PUBLIC,
                    Map.of()));
        }
        return new Conversation(
                "conv-1",
                "session-1",
                new com.chugalkhorbandar.domain.conversation.ConversationCharacter(
                        "c1", "Alpha", List.of("Alpha"), "Rabbitu", null),
                Instant.now(),
                Instant.now(),
                ConversationStatus.ACTIVE,
                messages);
    }

    private static LLMGenerateResult llmResult(String reply) {
        return new LLMGenerateResult(
                new LLMProviderInfo(LLMProviderType.MOCK, "Mock", "Mock provider", true, "mock-bandar"),
                null,
                new ProviderResponse(reply, new ProviderTokenUsage(1, 2, 3), Map.of(), 10, "stop"));
    }
}
