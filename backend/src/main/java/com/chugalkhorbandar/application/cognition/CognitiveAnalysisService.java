package com.chugalkhorbandar.application.cognition;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactService;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxService;
import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.application.conversation.director.ConversationDirectorService;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.SessionService;
import com.chugalkhorbandar.domain.cognition.ports.CognitiveAnalysisRepository;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ports.ConversationMessageRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class CognitiveAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CognitiveAnalysisService.class);

    private final SessionService sessionService;
    private final CognitiveAnalysisEngine engine;
    private final CognitiveAnalysisRepository repository;
    private final CognitiveAnalysisGenerationStore generationStore;
    private final CognitiveAnalysisProperties properties;
    private final CognitiveAnalysisProviderRegistry providerRegistry;
    private final WorkingMemoryService workingMemoryService;
    private final BehaviorEngineService behaviorEngineService;
    private final ConversationDirectorService conversationDirectorService;
    private final ConversationArtifactService conversationArtifactService;
    private final ConversationMessageRepository messages;
    private final WorldStatusQueryService worldStatusQueryService;
    private final MemoryInboxService memoryInboxService;

    public CognitiveAnalysisService(
            SessionService sessionService,
            CognitiveAnalysisEngine engine,
            CognitiveAnalysisRepository repository,
            CognitiveAnalysisGenerationStore generationStore,
            CognitiveAnalysisProperties properties,
            CognitiveAnalysisProviderRegistry providerRegistry,
            @Lazy WorkingMemoryService workingMemoryService,
            @Lazy BehaviorEngineService behaviorEngineService,
            @Lazy ConversationDirectorService conversationDirectorService,
            @Lazy ConversationArtifactService conversationArtifactService,
            ConversationMessageRepository messages,
            WorldStatusQueryService worldStatusQueryService,
            @Lazy MemoryInboxService memoryInboxService) {
        this.sessionService = sessionService;
        this.engine = engine;
        this.repository = repository;
        this.generationStore = generationStore;
        this.properties = properties;
        this.providerRegistry = providerRegistry;
        this.workingMemoryService = workingMemoryService;
        this.behaviorEngineService = behaviorEngineService;
        this.conversationDirectorService = conversationDirectorService;
        this.conversationArtifactService = conversationArtifactService;
        this.messages = messages;
        this.worldStatusQueryService = worldStatusQueryService;
        this.memoryInboxService = memoryInboxService;
    }

    public void analyzeCompletedTurn(String sessionId, Conversation conversation, String latestUserMessage) {
        if (!properties.isEnabled()) {
            return;
        }
        long start = System.nanoTime();
        ChatSession session = sessionService.requireSession(sessionId);
        String characterId = session.currentCharacter().id();
        String conversationId = conversation.conversationId();
        String providerName = providerRegistry.activeProvider().providerName();
        try {
            CognitiveAnalysisInput input = buildInput(sessionId, session, conversation);
            CognitiveAnalysisResult result = engine.analyze(characterId, input);
            repository.save(result);
            long executionTimeMs = elapsedMs(start);
            generationStore.saveSuccess(result, executionTimeMs);
            memoryInboxService.ingestForCompletedTurn(sessionId, conversationId, result);
        } catch (Exception exception) {
            long executionTimeMs = elapsedMs(start);
            String errorMessage = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
            LOGGER.warn(
                    "Cognitive analysis failed for conversation {} character {}: {}",
                    conversationId,
                    characterId,
                    errorMessage,
                    exception);
            generationStore.saveFailure(characterId, conversationId, providerName, errorMessage, executionTimeMs);
            repository.saveDiagnostic(new CognitiveAnalysisDiagnostic(
                    UUID.randomUUID().toString(),
                    characterId,
                    conversationId,
                    providerName,
                    errorMessage,
                    executionTimeMs,
                    Instant.now()));
            memoryInboxService.ingestForCompletedTurn(sessionId, conversationId, null);
        }
    }

    public Optional<CognitiveAnalysisResult> getLatestForSession(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return repository.findLatestByCharacterId(session.currentCharacter().id());
    }

    public Optional<CognitiveAnalysisResult> getForConversation(String sessionId, String conversationId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return repository.findByConversationId(session.currentCharacter().id(), conversationId);
    }

    public List<Observation> listObservationsForSession(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return repository.findAllByCharacterId(session.currentCharacter().id()).stream()
                .flatMap(result -> result.observations().stream())
                .toList();
    }

    public List<Recommendation> listRecommendationsForSession(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return repository.findAllByCharacterId(session.currentCharacter().id()).stream()
                .flatMap(result -> result.recommendations().stream())
                .toList();
    }

    public Optional<CognitiveAnalysisExecutionSnapshot> getLatestExecution(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return generationStore.findByCharacterId(session.currentCharacter().id());
    }

    public List<CognitiveAnalysisResult> listAllForDeveloper(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return repository.findAllByCharacterId(session.currentCharacter().id());
    }

    private CognitiveAnalysisInput buildInput(String sessionId, ChatSession session, Conversation conversation) {
        List<ConversationMessage> transcript = messages.findByConversationIdOrdered(conversation.conversationId());
        List<ConversationArtifact> artifacts = conversationArtifactService.listForSession(sessionId).stream()
                .filter(artifact -> conversation.conversationId().equals(artifact.conversationId()))
                .toList();
        var workingMemory = workingMemoryService
                .find(sessionId)
                .map(snapshot -> snapshot.memory())
                .orElse(null);
        var behaviorProfile = behaviorEngineService
                .getCurrentProfile(sessionId)
                .map(snapshot -> snapshot.profile())
                .orElse(null);
        var planSnapshot = conversationDirectorService.getCurrentPlan(sessionId).orElse(null);
        var worldStatus = worldStatusQueryService.getStatus();
        String runtimeWorldSummary = String.format(
                "status=%s, bootstrap=%s, characters=%d, stories=%d, persistence=%s",
                worldStatus.status(),
                worldStatus.bootstrapVersion(),
                worldStatus.characters(),
                worldStatus.stories(),
                worldStatus.persistenceProvider());
        return new CognitiveAnalysisInput(
                session.currentCharacter(),
                conversation,
                transcript,
                artifacts,
                workingMemory,
                behaviorProfile,
                planSnapshot,
                runtimeWorldSummary);
    }

    private static long elapsedMs(long startNano) {
        return Math.max(1, (System.nanoTime() - startNano) / 1_000_000);
    }
}
