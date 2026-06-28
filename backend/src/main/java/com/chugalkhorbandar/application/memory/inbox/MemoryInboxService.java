package com.chugalkhorbandar.application.memory.inbox;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactService;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.SessionService;
import com.chugalkhorbandar.domain.memory.inbox.ports.MemoryInboxRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MemoryInboxService {

    private final SessionService sessionService;
    private final MemoryInboxEngine engine;
    private final MemoryInboxRepository repository;
    private final MemoryInboxProperties properties;
    private final MemoryInboxGenerationStore generationStore;
    private final ConversationArtifactService conversationArtifactService;
    private final WorldStatusQueryService worldStatusQueryService;

    public MemoryInboxService(
            SessionService sessionService,
            MemoryInboxEngine engine,
            MemoryInboxRepository repository,
            MemoryInboxProperties properties,
            MemoryInboxGenerationStore generationStore,
            @Lazy ConversationArtifactService conversationArtifactService,
            WorldStatusQueryService worldStatusQueryService) {
        this.sessionService = sessionService;
        this.engine = engine;
        this.repository = repository;
        this.properties = properties;
        this.generationStore = generationStore;
        this.conversationArtifactService = conversationArtifactService;
        this.worldStatusQueryService = worldStatusQueryService;
    }

    public void ingestForCompletedTurn(String sessionId, String conversationId, CognitiveAnalysisResult analysis) {
        if (!properties.isEnabled()) {
            return;
        }
        ChatSession session = sessionService.requireSession(sessionId);
        String characterId = session.currentCharacter().id();
        Instant now = Instant.now();
        expireAndArchive(characterId, now);
        List<MemoryInboxItem> existing = repository.findByOwnerCharacterId(characterId);
        List<ConversationArtifact> artifacts = conversationArtifactService.listForSession(sessionId).stream()
                .filter(artifact -> conversationId.equals(artifact.conversationId()))
                .toList();
        var worldStatus = worldStatusQueryService.getStatus();
        String runtimeWorldSummary = String.format(
                "status=%s, bootstrap=%s, characters=%d",
                worldStatus.status(), worldStatus.bootstrapVersion(), worldStatus.characters());
        MemoryInboxEngineInput input = new MemoryInboxEngineInput(
                characterId, conversationId, artifacts, analysis, runtimeWorldSummary, now, existing);
        MemoryInboxGenerationSnapshot generation = engine.generate(input);
        generationStore.save(generation);
        List<MemoryInboxItem> active = activeItems(existing, now);
        for (MemoryInboxItem candidate : generation.generatedItems()) {
            if (active.size() >= properties.getMaximumItems()) {
                break;
            }
            Optional<MemoryInboxItem> persisted = persistWithDeduplication(candidate, existing);
            persisted.ifPresent(item -> {
                active.add(item);
                existing.add(item);
            });
        }
    }

    public List<MemoryInboxItem> listForSession(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        Instant now = Instant.now();
        expireAndArchive(session.currentCharacter().id(), now);
        return sortForDisplay(repository.findByOwnerCharacterId(session.currentCharacter().id()));
    }

    public MemoryInboxItem getForSession(String sessionId, String itemId) {
        return requireOwnedItem(sessionId, itemId);
    }

    public MemoryInboxItem review(String sessionId, String itemId) {
        MemoryInboxItem item = requireOwnedItem(sessionId, itemId);
        Instant now = Instant.now();
        if (item.status() == MemoryInboxStatus.NEW) {
            return repository.save(item.withStatus(MemoryInboxStatus.REVIEWED, now).withTraceAppend("reviewed"));
        }
        return item;
    }

    public MemoryInboxItem discard(String sessionId, String itemId) {
        MemoryInboxItem item = requireOwnedItem(sessionId, itemId);
        Instant now = Instant.now();
        MemoryInboxItem discarded =
                repository.save(item.withStatus(MemoryInboxStatus.DISCARDED, now).withTraceAppend("discarded"));
        return archive(discarded, now);
    }

    public Optional<MemoryInboxGenerationSnapshot> getLatestGeneration(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return generationStore.findByCharacterId(session.currentCharacter().id());
    }

    public List<MemoryInboxItem> listAllForDeveloper(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        Instant now = Instant.now();
        expireAndArchive(session.currentCharacter().id(), now);
        return sortForDisplay(repository.findByOwnerCharacterId(session.currentCharacter().id()));
    }

    private Optional<MemoryInboxItem> persistWithDeduplication(
            MemoryInboxItem candidate, List<MemoryInboxItem> existing) {
        if (!properties.isDeduplicationEnabled()) {
            return Optional.of(repository.save(candidate));
        }

        Optional<MemoryInboxItem> sameSource = repository.findByOwnerCharacterIdAndSourceAndSourceId(
                candidate.ownerCharacterId(), candidate.source().name(), candidate.sourceId());
        if (sameSource.isPresent()) {
            return Optional.empty();
        }

        for (String artifactId : candidate.artifactIds()) {
            if (artifactAlreadyPromoted(existing, artifactId)) {
                return Optional.empty();
            }
        }

        Optional<MemoryInboxItem> identicalObservation = findIdenticalObservation(candidate, existing);
        if (identicalObservation.isPresent()) {
            MemoryInboxItem merged = identicalObservation.get()
                    .withMergedArtifactIds(candidate.artifactIds())
                    .withTraceAppend("merged-duplicate-observation");
            repository.save(merged);
            return Optional.empty();
        }

        return Optional.of(repository.save(candidate));
    }

    private static boolean artifactAlreadyPromoted(List<MemoryInboxItem> existing, String artifactId) {
        return existing.stream()
                .anyMatch(item -> item.status() == MemoryInboxStatus.PROMOTED
                        && item.artifactIds().contains(artifactId));
    }

    private static Optional<MemoryInboxItem> findIdenticalObservation(
            MemoryInboxItem candidate, List<MemoryInboxItem> existing) {
        if (candidate.source() != MemoryInboxSource.COGNITIVE_OBSERVATION) {
            return Optional.empty();
        }
        return existing.stream()
                .filter(item -> item.source() == MemoryInboxSource.COGNITIVE_OBSERVATION)
                .filter(item -> item.type().equals(candidate.type()))
                .filter(item -> item.summary().equals(candidate.summary()))
                .filter(item -> item.status() != MemoryInboxStatus.DISCARDED
                        && item.status() != MemoryInboxStatus.ARCHIVED)
                .findFirst();
    }

    private MemoryInboxItem requireOwnedItem(String sessionId, String itemId) {
        ChatSession session = sessionService.requireSession(sessionId);
        MemoryInboxItem item = repository.findById(itemId).orElseThrow(MemoryInboxNotFoundException::new);
        if (!session.currentCharacter().id().equals(item.ownerCharacterId())) {
            throw new MemoryInboxNotFoundException();
        }
        return item;
    }

    private MemoryInboxItem archive(MemoryInboxItem item, Instant now) {
        return repository.save(item.withStatus(MemoryInboxStatus.ARCHIVED, now).withTraceAppend("archived"));
    }

    private void expireAndArchive(String characterId, Instant now) {
        for (MemoryInboxItem item : repository.findByOwnerCharacterId(characterId)) {
            if ((item.status() == MemoryInboxStatus.NEW || item.status() == MemoryInboxStatus.REVIEWED)
                    && item.expiresAt().isBefore(now)) {
                MemoryInboxItem expired =
                        repository.save(item.withStatus(MemoryInboxStatus.EXPIRED, now).withTraceAppend("expired"));
                archive(expired, now);
            } else if (item.status() == MemoryInboxStatus.DISCARDED || item.status() == MemoryInboxStatus.EXPIRED) {
                archive(item, now);
            }
        }
    }

    private static List<MemoryInboxItem> activeItems(List<MemoryInboxItem> items, Instant now) {
        List<MemoryInboxItem> active = new ArrayList<>();
        for (MemoryInboxItem item : items) {
            if ((item.status() == MemoryInboxStatus.NEW || item.status() == MemoryInboxStatus.REVIEWED)
                    && !item.expiresAt().isBefore(now)) {
                active.add(item);
            }
        }
        return active;
    }

    private static List<MemoryInboxItem> sortForDisplay(List<MemoryInboxItem> items) {
        return items.stream()
                .sorted(Comparator.comparingInt(MemoryInboxService::importanceRank).reversed()
                        .thenComparing(MemoryInboxItem::createdAt, Comparator.reverseOrder()))
                .toList();
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
