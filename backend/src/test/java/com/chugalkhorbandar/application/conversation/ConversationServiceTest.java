package com.chugalkhorbandar.application.conversation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryCharacterCredentialRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryConversationMessageRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryConversationRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryConversationStore;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.llm.LLMGenerateResult;
import com.chugalkhorbandar.application.llm.LLMProviderInfo;
import com.chugalkhorbandar.application.llm.LLMProviderType;
import com.chugalkhorbandar.application.llm.LLMService;
import com.chugalkhorbandar.application.llm.ProviderResponse;
import com.chugalkhorbandar.application.llm.ProviderTokenUsage;
import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.application.conversation.director.ConversationArc;
import com.chugalkhorbandar.application.conversation.director.ConversationDirectorService;
import com.chugalkhorbandar.application.conversation.director.ConversationEnergy;
import com.chugalkhorbandar.application.conversation.director.ConversationGoal;
import com.chugalkhorbandar.application.conversation.director.ConversationOutcome;
import com.chugalkhorbandar.application.conversation.director.ConversationPlan;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanExecutor;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanningTrace;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
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
import com.chugalkhorbandar.domain.conversation.Visibility;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    private final InMemoryWorldStore worldStore = new InMemoryWorldStore();
    private final InMemoryCharacterCredentialRepository credentials = new InMemoryCharacterCredentialRepository();
    private final InMemoryConversationStore conversationStore = new InMemoryConversationStore();
    private final InMemorySessionStore sessionStore = new InMemorySessionStore(List.of());
    private final ChugalkhorProperties properties = new ChugalkhorProperties();

    @Mock
    private NotificationService notificationService;

    private SessionService sessionService;

    @Mock
    private LLMService llmService;

    @Mock
    private WorkingMemoryService workingMemoryService;

    @Mock
    private ConversationDirectorService conversationDirectorService;

    @Mock
    private ConversationPlanExecutor conversationPlanExecutor;

    @Mock
    private BehaviorEngineService behaviorEngineService;

    @Mock
    private ConversationArtifactService conversationArtifactService;

    @Mock
    private CognitiveAnalysisTrigger cognitiveAnalysisTrigger;

    private ConversationService conversationService;

    private InMemoryConversationMessageRepository messageRepository;

    private String sessionId;

    @BeforeEach
    void seedSession() {
        sessionService = new SessionService(
                new InMemoryWorldRepositoryProvider(worldStore),
                credentials,
                new EntityReferenceResolver(new InMemoryWorldRepositoryProvider(worldStore)),
                sessionStore,
                properties,
                notificationService,
                new com.chugalkhorbandar.application.session.CharacterPresenceStore());
        messageRepository = new InMemoryConversationMessageRepository(conversationStore);
        conversationService = new ConversationService(
                sessionService,
                new InMemoryConversationRepository(conversationStore),
                messageRepository,
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
        sessionId = sessionService.login("Alpha", "secret").sessionId();
    }

    @Test
    void startsConversationForSession() {
        Conversation conversation = conversationService.startConversation(sessionId);

        assertThat(conversation.sessionId()).isEqualTo(sessionId);
        assertThat(conversation.status()).isEqualTo(ConversationStatus.ACTIVE);
        assertThat(conversation.currentCharacter().displayName()).isEqualTo("Alpha");
        assertThat(conversation.messages()).isEmpty();
    }

    @Test
    void startConversationReturnsExistingActiveConversation() {
        Conversation first = conversationService.startConversation(sessionId);
        Conversation second = conversationService.startConversation(sessionId);

        assertThat(second.conversationId()).isEqualTo(first.conversationId());
    }

    @Test
    void appendUserMessageGeneratesBandarReplyThroughLlmPipeline() {
        conversationService.startConversation(sessionId);
        ConversationPlanSnapshot planSnapshot = planSnapshot(1);
        String conversationId = conversationService.currentConversation(sessionId).conversationId();
        ConversationMessage bandarReply = bandarMessage(conversationId, "Bandar reply from pipeline.");
        when(conversationDirectorService.plan(eq(sessionId), eq("Hello"))).thenReturn(planSnapshot);
        when(conversationPlanExecutor.execute(eq(planSnapshot), eq(sessionId), eq("Hello"), any()))
                .thenReturn(List.of(bandarReply));

        List<ConversationMessage> appended = conversationService.appendUserMessage(sessionId, "Hello");

        assertThat(appended).hasSize(2);
        assertThat(appended.get(0).sender()).isEqualTo(Sender.USER);
        assertThat(appended.get(0).content()).isEqualTo("Hello");
        assertThat(appended.get(1).sender()).isEqualTo(Sender.BANDAR);
        assertThat(appended.get(1).content()).isEqualTo("Bandar reply from pipeline.");
    }

    @Test
    void messagesRemainOrdered() {
        conversationService.startConversation(sessionId);
        when(conversationDirectorService.plan(eq(sessionId), eq("One"))).thenReturn(planSnapshot(1));
        when(conversationDirectorService.plan(eq(sessionId), eq("Two"))).thenReturn(planSnapshot(1));
        when(conversationPlanExecutor.execute(any(), eq(sessionId), eq("One"), any()))
                .thenAnswer(invocation -> List.of(messageRepository.append(
                        bandarMessage(invocation.getArgument(3, Conversation.class).conversationId(), "Reply one"))));
        when(conversationPlanExecutor.execute(any(), eq(sessionId), eq("Two"), any()))
                .thenAnswer(invocation -> List.of(messageRepository.append(
                        bandarMessage(invocation.getArgument(3, Conversation.class).conversationId(), "Reply two"))));
        conversationService.appendUserMessage(sessionId, "One");
        conversationService.appendUserMessage(sessionId, "Two");

        List<ConversationMessage> messages = conversationService.getMessages(sessionId);

        assertThat(messages).hasSize(4);
        assertThat(messages.get(0).content()).isEqualTo("One");
        assertThat(messages.get(1).content()).isEqualTo("Reply one");
        assertThat(messages.get(2).content()).isEqualTo("Two");
        assertThat(messages.get(3).content()).isEqualTo("Reply two");
    }

    @Test
    void windowReturnsRecentMessagesAndTurn() {
        conversationService.startConversation(sessionId);
        when(conversationDirectorService.plan(eq(sessionId), eq("Hello"))).thenReturn(planSnapshot(1));
        when(conversationPlanExecutor.execute(any(), eq(sessionId), eq("Hello"), any()))
                .thenAnswer(invocation -> List.of(messageRepository.append(
                        bandarMessage(invocation.getArgument(3, Conversation.class).conversationId(), "Bandar says hi"))));
        conversationService.appendUserMessage(sessionId, "Hello");

        ConversationWindow window = conversationService.getWindow(sessionId, 1);

        assertThat(window.messages()).hasSize(1);
        assertThat(window.messages().get(0).sender()).isEqualTo(Sender.BANDAR);
        assertThat(window.currentTurn()).isEqualTo(Sender.USER);
        assertThat(window.tokenEstimate()).isGreaterThan(0);
    }

    @Test
    void closeConversationEndsActiveConversation() {
        conversationService.startConversation(sessionId);
        conversationService.closeConversation(sessionId);

        assertThatThrownBy(() -> conversationService.currentConversation(sessionId))
                .isInstanceOf(ConversationNotFoundException.class);
    }

    private static ConversationPlanSnapshot planSnapshot(int expectedMessageCount) {
        return ConversationPlanSnapshot.planned(
                "session-1",
                new ConversationPlan(
                        ConversationGoal.SMALL_TALK,
                        0.65,
                        true,
                        ConversationEnergy.LOW,
                        ConversationArc.SMALL_TALK,
                        expectedMessageCount,
                        List.of(),
                        false,
                        false,
                        false,
                        false,
                        false,
                        "Thoughtful",
                        ConversationOutcome.UNRESOLVED,
                        java.time.Instant.now(),
                        false,
                        false,
                        null,
                        null),
                new ConversationPlanningTrace(List.of()));
    }

    private static ConversationMessage bandarMessage(String conversationId, String content) {
        return new ConversationMessage(
                UUID.randomUUID().toString(),
                conversationId,
                Sender.BANDAR,
                java.time.Instant.now(),
                content,
                Visibility.PUBLIC,
                Map.of());
    }

    private static LLMGenerateResult llmResult(String reply) {
        return new LLMGenerateResult(
                new LLMProviderInfo(LLMProviderType.MOCK, "Mock", "Mock provider", true, "mock-bandar"),
                null,
                new ProviderResponse(reply, new ProviderTokenUsage(1, 2, 3), Map.of(), 10, "stop"));
    }
}
