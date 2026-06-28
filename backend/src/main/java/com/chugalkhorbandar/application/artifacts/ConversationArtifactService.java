package com.chugalkhorbandar.application.artifacts;

import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.application.behavior.BehaviorProfile;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisProperties;
import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxService;
import com.chugalkhorbandar.application.conversation.director.ConversationDirectorService;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.SessionService;
import com.chugalkhorbandar.domain.artifacts.ports.ConversationArtifactRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ConversationArtifactService {

    private final SessionService sessionService;
    private final ConversationArtifactEngine artifactEngine;
    private final ConversationArtifactRepository artifactRepository;
    private final ConversationArtifactProperties properties;
    private final WorkingMemoryService workingMemoryService;
    private final BehaviorEngineService behaviorEngineService;
    private final ConversationDirectorService conversationDirectorService;
    private final WorldStatusQueryService worldStatusQueryService;
    private final ConversationArtifactGenerationStore generationStore;
    private final CognitiveAnalysisProperties cognitiveAnalysisProperties;
    private final MemoryInboxService memoryInboxService;

    public ConversationArtifactService(
            SessionService sessionService,
            ConversationArtifactEngine artifactEngine,
            ConversationArtifactRepository artifactRepository,
            ConversationArtifactProperties properties,
            @Lazy WorkingMemoryService workingMemoryService,
            @Lazy BehaviorEngineService behaviorEngineService,
            @Lazy ConversationDirectorService conversationDirectorService,
            WorldStatusQueryService worldStatusQueryService,
            ConversationArtifactGenerationStore generationStore,
            CognitiveAnalysisProperties cognitiveAnalysisProperties,
            @Lazy MemoryInboxService memoryInboxService) {
        this.sessionService = sessionService;
        this.artifactEngine = artifactEngine;
        this.artifactRepository = artifactRepository;
        this.properties = properties;
        this.workingMemoryService = workingMemoryService;
        this.behaviorEngineService = behaviorEngineService;
        this.conversationDirectorService = conversationDirectorService;
        this.worldStatusQueryService = worldStatusQueryService;
        this.generationStore = generationStore;
        this.cognitiveAnalysisProperties = cognitiveAnalysisProperties;
        this.memoryInboxService = memoryInboxService;
    }

    public void processCompletedTurn(String sessionId, com.chugalkhorbandar.domain.conversation.Conversation conversation, String latestUserMessage) {
        ChatSession session = sessionService.requireSession(sessionId);
        Optional<ConversationPlanSnapshot> planSnapshot = conversationDirectorService.getCurrentPlan(sessionId);
        if (planSnapshot.isEmpty()) {
            return;
        }
        String characterId = session.currentCharacter().id();
        Instant now = Instant.now();
        expireAndArchive(characterId, now);
        List<ConversationArtifact> existing = artifactRepository.findRelevantForCharacter(characterId);
        var workingMemory = workingMemoryService
                .find(sessionId)
                .map(snapshot -> snapshot.memory())
                .orElse(null);
        BehaviorProfile behaviorProfile = behaviorEngineService
                .getCurrentProfile(sessionId)
                .map(snapshot -> snapshot.profile())
                .orElse(null);
        var worldStatus = worldStatusQueryService.getStatus();
        RuntimeWorldContext runtimeWorld = new RuntimeWorldContext(
                worldStatus.status(),
                worldStatus.bootstrapVersion(),
                worldStatus.characters(),
                worldStatus.stories(),
                List.of());
        ConversationArtifactEngineInput input = new ConversationArtifactEngineInput(
                session.currentCharacter(),
                conversation,
                workingMemory,
                planSnapshot.get(),
                behaviorProfile,
                runtimeWorld,
                latestUserMessage,
                now,
                existing);
        ConversationArtifactGenerationSnapshot generation = artifactEngine.generate(input);
        generationStore.save(generation);
        List<ConversationArtifact> active = activeArtifacts(existing, now);
        for (ConversationArtifact artifact : generation.generatedArtifacts()) {
            if (active.size() >= properties.getMaximumActiveArtifacts()) {
                break;
            }
            ConversationArtifact activated = activate(artifact, now);
            artifactRepository.save(activated);
            active.add(activated);
        }
        if (!cognitiveAnalysisProperties.isEnabled()) {
            memoryInboxService.ingestForCompletedTurn(sessionId, conversation.conversationId(), null);
        }
    }

    public List<ConversationArtifact> listForSession(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        Instant now = Instant.now();
        expireAndArchive(session.currentCharacter().id(), now);
        return sortForDisplay(artifactRepository.findRelevantForCharacter(session.currentCharacter().id()));
    }

    public ConversationArtifact getForSession(String sessionId, String artifactId) {
        ConversationArtifact artifact = requireOwnedArtifact(sessionId, artifactId);
        return artifact;
    }

    public ConversationArtifact fulfill(String sessionId, String artifactId) {
        ConversationArtifact artifact = requireOwnedArtifact(sessionId, artifactId);
        Instant now = Instant.now();
        if (artifact.status() == ConversationArtifactStatus.FULFILLED
                || artifact.status() == ConversationArtifactStatus.ARCHIVED) {
            return artifact;
        }
        ConversationArtifact fulfilled = artifactRepository.save(artifact
                .withStatus(ConversationArtifactStatus.FULFILLED, now)
                .withTraceAppend("fulfilled"));
        return fulfilled;
    }

    public ConversationArtifact cancel(String sessionId, String artifactId) {
        ConversationArtifact artifact = requireOwnedArtifact(sessionId, artifactId);
        Instant now = Instant.now();
        ConversationArtifact cancelled = artifactRepository.save(artifact
                .withStatus(ConversationArtifactStatus.CANCELLED, now)
                .withTraceAppend("cancelled"));
        return archive(cancelled, now);
    }

    public Optional<ConversationArtifactGenerationSnapshot> getLatestGeneration(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return generationStore.findByCharacterId(session.currentCharacter().id());
    }

    public List<ConversationArtifact> listAllForDeveloper(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        Instant now = Instant.now();
        expireAndArchive(session.currentCharacter().id(), now);
        return sortForDisplay(artifactRepository.findAllForCharacter(session.currentCharacter().id()));
    }

    private ConversationArtifact requireOwnedArtifact(String sessionId, String artifactId) {
        ChatSession session = sessionService.requireSession(sessionId);
        ConversationArtifact artifact = artifactRepository
                .findById(artifactId)
                .orElseThrow(ConversationArtifactNotFoundException::new);
        String characterId = session.currentCharacter().id();
        if (!characterId.equals(artifact.ownerCharacterId())
                && !characterId.equals(artifact.recipientCharacterId())) {
            throw new ConversationArtifactNotFoundException();
        }
        return artifact;
    }

    private ConversationArtifact activate(ConversationArtifact artifact, Instant now) {
        return artifact
                .withStatus(ConversationArtifactStatus.ACTIVE, now)
                .withTraceAppend("activated");
    }

    private ConversationArtifact archive(ConversationArtifact artifact, Instant now) {
        return artifactRepository.save(artifact
                .withStatus(ConversationArtifactStatus.ARCHIVED, now)
                .withTraceAppend("archived"));
    }

    private void expireAndArchive(String characterId, Instant now) {
        for (ConversationArtifact artifact : artifactRepository.findRelevantForCharacter(characterId)) {
            if ((artifact.status() == ConversationArtifactStatus.NEW
                            || artifact.status() == ConversationArtifactStatus.ACTIVE)
                    && artifact.expiresAt().isBefore(now)) {
                ConversationArtifact expired = artifactRepository.save(artifact
                        .withStatus(ConversationArtifactStatus.EXPIRED, now)
                        .withTraceAppend("expired"));
                archive(expired, now);
            } else if (artifact.status() == ConversationArtifactStatus.CANCELLED
                    || artifact.status() == ConversationArtifactStatus.EXPIRED) {
                archive(artifact, now);
            }
        }
    }

    private static List<ConversationArtifact> activeArtifacts(List<ConversationArtifact> artifacts, Instant now) {
        List<ConversationArtifact> active = new ArrayList<>();
        for (ConversationArtifact artifact : artifacts) {
            if ((artifact.status() == ConversationArtifactStatus.NEW
                            || artifact.status() == ConversationArtifactStatus.ACTIVE)
                    && !artifact.expiresAt().isBefore(now)) {
                active.add(artifact);
            }
        }
        return active;
    }

    private static List<ConversationArtifact> sortForDisplay(List<ConversationArtifact> artifacts) {
        return artifacts.stream()
                .sorted(Comparator.comparingInt(ConversationArtifactService::priorityRank).reversed()
                        .thenComparing(ConversationArtifact::createdAt, Comparator.reverseOrder()))
                .toList();
    }

    private static int priorityRank(ConversationArtifact artifact) {
        return switch (artifact.priority()) {
            case CRITICAL -> 4;
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }
}
