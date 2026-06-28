package com.chugalkhorbandar.application.chronicle;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.memory.consolidation.LongTermMemoryCandidate;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import com.chugalkhorbandar.domain.artifacts.ports.ConversationArtifactRepository;
import com.chugalkhorbandar.domain.chronicle.ports.ChronicleRepository;
import com.chugalkhorbandar.domain.memory.consolidation.ports.LongTermMemoryCandidateRepository;
import com.chugalkhorbandar.domain.memory.inbox.ports.MemoryInboxRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChronicleWriterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChronicleWriterService.class);

    private final ChronicleWriterProperties properties;
    private final ChronicleWriter writer;
    private final ChronicleRepository chronicleRepository;
    private final LongTermMemoryCandidateRepository candidateRepository;
    private final MemoryInboxRepository inboxRepository;
    private final ConversationArtifactRepository artifactRepository;
    private final WorldRepositoryProvider worldRepositoryProvider;
    private final ChronicleWriterGenerationStore generationStore;

    public ChronicleWriterService(
            ChronicleWriterProperties properties,
            ChronicleWriter writer,
            ChronicleRepository chronicleRepository,
            LongTermMemoryCandidateRepository candidateRepository,
            MemoryInboxRepository inboxRepository,
            ConversationArtifactRepository artifactRepository,
            WorldRepositoryProvider worldRepositoryProvider,
            ChronicleWriterGenerationStore generationStore) {
        this.properties = properties;
        this.writer = writer;
        this.chronicleRepository = chronicleRepository;
        this.candidateRepository = candidateRepository;
        this.inboxRepository = inboxRepository;
        this.artifactRepository = artifactRepository;
        this.worldRepositoryProvider = worldRepositoryProvider;
        this.generationStore = generationStore;
    }

    public ChronicleWriteResult writeChronicles() {
        if (!properties.isEnabled() || !properties.isWriterEnabled()) {
            throw new IllegalStateException("Chronicle writer is disabled");
        }
        Instant startedAt = Instant.now();
        String runId = UUID.randomUUID().toString();
        LocalDate currentDate = startedAt.atZone(ZoneOffset.UTC).toLocalDate();

        List<LongTermMemoryCandidate> candidates = candidateRepository.findAllOrderByCreatedAtDesc();
        Map<String, Integer> existingVersions = buildExistingVersions(candidates);
        Map<String, List<MemoryInboxItem>> inboxByCandidate = buildInboxItems(candidates);
        Map<String, List<ConversationArtifact>> artifactsByCandidate = buildArtifacts(inboxByCandidate);
        Map<String, String> characterNames = buildCharacterNames(candidates, artifactsByCandidate);

        ChronicleWriteResult result = writer.write(
                runId,
                startedAt,
                candidates,
                currentDate,
                characterNames,
                inboxByCandidate,
                artifactsByCandidate,
                existingVersions);

        List<Chronicle> persisted = new ArrayList<>();
        for (Chronicle chronicle : result.chronicles()) {
            persisted.add(chronicleRepository.save(chronicle));
        }

        ChronicleWriteResult persistedResult = new ChronicleWriteResult(
                result.runId(),
                result.startedAt(),
                result.completedAt(),
                result.durationMs(),
                result.candidatesProcessed(),
                persisted.size(),
                result.skipped(),
                persisted,
                result.trace());

        generationStore.save(persistedResult);
        LOGGER.info(
                "Chronicle write run {} complete: written={}, skipped={}",
                runId,
                persisted.size(),
                result.skipped());
        return persistedResult;
    }

    public ChronicleWriteResult writeChroniclesFromCandidates(List<LongTermMemoryCandidate> candidates) {
        Instant startedAt = Instant.now();
        String runId = UUID.randomUUID().toString();
        LocalDate currentDate = startedAt.atZone(ZoneOffset.UTC).toLocalDate();
        Map<String, Integer> existingVersions = buildExistingVersions(candidates);
        Map<String, List<MemoryInboxItem>> inboxByCandidate = buildInboxItems(candidates);
        Map<String, List<ConversationArtifact>> artifactsByCandidate = buildArtifacts(inboxByCandidate);
        Map<String, String> characterNames = buildCharacterNames(candidates, artifactsByCandidate);

        ChronicleWriteResult result = writer.write(
                runId,
                startedAt,
                candidates,
                currentDate,
                characterNames,
                inboxByCandidate,
                artifactsByCandidate,
                existingVersions);

        List<Chronicle> persisted = result.chronicles().stream().map(chronicleRepository::save).toList();
        ChronicleWriteResult persistedResult = new ChronicleWriteResult(
                result.runId(),
                result.startedAt(),
                result.completedAt(),
                result.durationMs(),
                result.candidatesProcessed(),
                persisted.size(),
                result.skipped(),
                persisted,
                result.trace());
        generationStore.save(persistedResult);
        return persistedResult;
    }

    public List<Chronicle> listChronicles() {
        return chronicleRepository.findAllOrderByCreatedAtDesc();
    }

    public Chronicle getChronicle(String id) {
        return chronicleRepository.findById(id).orElseThrow(ChronicleNotFoundException::new);
    }

    public List<Chronicle> listByCategory(ChronicleCategory category) {
        return chronicleRepository.findByCategoryOrderByCreatedAtDesc(category);
    }

    public List<Chronicle> listByVisibility(ChronicleVisibility visibility) {
        return chronicleRepository.findByVisibilityOrderByCreatedAtDesc(visibility);
    }

    public Optional<ChronicleWriteResult> getLatestWriteRun() {
        return generationStore.getLatest();
    }

    private Map<String, Integer> buildExistingVersions(List<LongTermMemoryCandidate> candidates) {
        Map<String, Integer> versions = new HashMap<>();
        for (LongTermMemoryCandidate candidate : candidates) {
            int count = chronicleRepository.countByCandidateId(candidate.id());
            if (count > 0) {
                versions.put(candidate.id(), count);
            }
        }
        return versions;
    }

    private Map<String, List<MemoryInboxItem>> buildInboxItems(List<LongTermMemoryCandidate> candidates) {
        Map<String, List<MemoryInboxItem>> result = new LinkedHashMap<>();
        for (LongTermMemoryCandidate candidate : candidates) {
            List<MemoryInboxItem> items = new ArrayList<>();
            for (String inboxId : candidate.sourceInboxItems()) {
                inboxRepository.findById(inboxId).ifPresent(items::add);
            }
            result.put(candidate.id(), items);
        }
        return result;
    }

    private Map<String, List<ConversationArtifact>> buildArtifacts(
            Map<String, List<MemoryInboxItem>> inboxByCandidate) {
        Map<String, List<ConversationArtifact>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<MemoryInboxItem>> entry : inboxByCandidate.entrySet()) {
            List<ConversationArtifact> artifacts = new ArrayList<>();
            for (MemoryInboxItem item : entry.getValue()) {
                for (String artifactId : item.artifactIds()) {
                    artifactRepository.findById(artifactId).ifPresent(artifacts::add);
                }
            }
            result.put(entry.getKey(), artifacts);
        }
        return result;
    }

    private Map<String, String> buildCharacterNames(
            List<LongTermMemoryCandidate> candidates, Map<String, List<ConversationArtifact>> artifactsByCandidate) {
        Map<String, String> names = new LinkedHashMap<>();
        for (LongTermMemoryCandidate candidate : candidates) {
            resolveCharacterName(candidate.ownerCharacterId()).ifPresent(name -> names.put(candidate.ownerCharacterId(), name));
            for (ConversationArtifact artifact : artifactsByCandidate.getOrDefault(candidate.id(), List.of())) {
                resolveCharacterName(artifact.recipientCharacterId()).ifPresent(name -> names.put(artifact.recipientCharacterId(), name));
                resolveCharacterName(artifact.createdByCharacterId()).ifPresent(name -> names.put(artifact.createdByCharacterId(), name));
            }
        }
        return names;
    }

    private Optional<String> resolveCharacterName(String characterId) {
        if (characterId == null || characterId.isBlank()) {
            return Optional.empty();
        }
        return worldRepositoryProvider
                .characters()
                .findById(characterId)
                .map(character -> character.title() == null || character.title().isBlank() ? characterId : character.title());
    }
}
