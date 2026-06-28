package com.chugalkhorbandar.application.conversation;

import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactService;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisTrigger;
import com.chugalkhorbandar.application.conversation.director.ConversationDirectorService;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanExecutor;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.llm.LLMService;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionService;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.ConversationWindow;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import com.chugalkhorbandar.domain.conversation.ports.ConversationMessageRepository;
import com.chugalkhorbandar.domain.conversation.ports.ConversationRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {

    private final SessionService sessionService;
    private final ConversationRepository conversations;
    private final ConversationMessageRepository messages;
    private final LLMService llmService;
    private final WorkingMemoryService workingMemoryService;
    private final ConversationDirectorService conversationDirectorService;
    private final BehaviorEngineService behaviorEngineService;
    private final ConversationPlanExecutor conversationPlanExecutor;
    private final ConversationArtifactService conversationArtifactService;
    private final CognitiveAnalysisTrigger cognitiveAnalysisTrigger;

    public ConversationService(
            SessionService sessionService,
            ConversationRepository conversations,
            ConversationMessageRepository messages,
            @Lazy LLMService llmService,
            @Lazy WorkingMemoryService workingMemoryService,
            ConversationDirectorService conversationDirectorService,
            @Lazy BehaviorEngineService behaviorEngineService,
            @Lazy ConversationPlanExecutor conversationPlanExecutor,
            @Lazy ConversationArtifactService conversationArtifactService,
            @Lazy CognitiveAnalysisTrigger cognitiveAnalysisTrigger) {
        this.sessionService = sessionService;
        this.conversations = conversations;
        this.messages = messages;
        this.llmService = llmService;
        this.workingMemoryService = workingMemoryService;
        this.conversationDirectorService = conversationDirectorService;
        this.behaviorEngineService = behaviorEngineService;
        this.conversationPlanExecutor = conversationPlanExecutor;
        this.conversationArtifactService = conversationArtifactService;
        this.cognitiveAnalysisTrigger = cognitiveAnalysisTrigger;
    }

    public Conversation startConversation(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return conversations
                .findActiveBySessionId(sessionId)
                .map(this::hydrate)
                .orElseGet(() -> createConversation(session));
    }

    public Conversation currentConversation(String sessionId) {
        sessionService.requireSession(sessionId);
        return conversations
                .findActiveBySessionId(sessionId)
                .map(this::hydrate)
                .orElseThrow(ConversationNotFoundException::new);
    }

    public Optional<Conversation> findCurrentConversation(String sessionId) {
        sessionService.requireSession(sessionId);
        return conversations.findActiveBySessionId(sessionId).map(this::hydrate);
    }

    public List<ConversationMessage> appendUserMessage(String sessionId, String content) {
        Conversation conversation = currentConversation(sessionId);
        Instant now = Instant.now();
        String trimmed = content.trim();

        ConversationMessage userMessage = messages.append(new ConversationMessage(
                UUID.randomUUID().toString(),
                conversation.conversationId(),
                Sender.USER,
                now,
                trimmed,
                Visibility.PUBLIC,
                Map.of()));

        workingMemoryService.rebuild(sessionId);

        conversationDirectorService.cancelPendingExecution(sessionId, "New user message received");

        ConversationPlanSnapshot planSnapshot = conversationDirectorService.plan(sessionId, trimmed);
        behaviorEngineService.select(sessionId, trimmed, planSnapshot.plan());
        Conversation hydrated = hydrate(conversation);
        List<ConversationMessage> bandarMessages =
                conversationPlanExecutor.execute(planSnapshot, sessionId, trimmed, hydrated);

        conversationArtifactService.processCompletedTurn(sessionId, hydrated, trimmed);

        cognitiveAnalysisTrigger.schedule(sessionId, hydrated, trimmed);

        conversations.updateActivity(conversation.conversationId(), Instant.now());

        List<ConversationMessage> appended = new ArrayList<>();
        appended.add(userMessage);
        appended.addAll(bandarMessages);
        return appended;
    }

    public List<ConversationMessage> getMessages(String sessionId) {
        return new ArrayList<>(currentConversation(sessionId).messages());
    }

    public ConversationWindow getWindow(String sessionId, int maxMessages) {
        return ConversationWindowBuilder.build(currentConversation(sessionId), maxMessages);
    }

    public void closeConversation(String sessionId) {
        Conversation conversation = currentConversation(sessionId);
        conversations.updateStatus(conversation.conversationId(), ConversationStatus.CLOSED);
        workingMemoryService.delete(sessionId);
        conversationDirectorService.delete(sessionId);
        behaviorEngineService.delete(sessionId);
    }

    private Conversation createConversation(ChatSession session) {
        Instant now = Instant.now();
        Conversation conversation = new Conversation(
                UUID.randomUUID().toString(),
                session.sessionId(),
                toConversationCharacter(session.currentCharacter()),
                now,
                now,
                ConversationStatus.ACTIVE,
                List.of());
        return conversations.save(conversation);
    }

    private Conversation hydrate(Conversation conversation) {
        List<ConversationMessage> orderedMessages =
                messages.findByConversationIdOrdered(conversation.conversationId());
        return conversation.withMessages(orderedMessages);
    }

    private static ConversationCharacter toConversationCharacter(CurrentCharacter character) {
        return new ConversationCharacter(
                character.id(),
                character.displayName(),
                character.titles(),
                character.species(),
                character.homeTerritory());
    }
}
