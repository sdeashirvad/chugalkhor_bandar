package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.domain.artifacts.ports.ConversationArtifactRepository;
import com.chugalkhorbandar.domain.chronicle.ports.ChronicleRepository;
import com.chugalkhorbandar.domain.world.living.ports.WorldEventRepository;
import com.chugalkhorbandar.domain.world.living.ports.WorldTickHistoryRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LivingWorldService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LivingWorldService.class);

    private final LivingWorldProperties properties;
    private final LivingWorldEngine engine;
    private final WorldClock worldClock;
    private final WorldEventRepository worldEventRepository;
    private final WorldTickHistoryRepository tickHistoryRepository;
    private final ConversationArtifactRepository artifactRepository;
    private final ChronicleRepository chronicleRepository;
    private final WorldRepositoryProvider worldRepositoryProvider;
    private final LivingWorldGenerationStore generationStore;

    public LivingWorldService(
            LivingWorldProperties properties,
            LivingWorldEngine engine,
            WorldClock worldClock,
            WorldEventRepository worldEventRepository,
            WorldTickHistoryRepository tickHistoryRepository,
            ConversationArtifactRepository artifactRepository,
            ChronicleRepository chronicleRepository,
            WorldRepositoryProvider worldRepositoryProvider,
            LivingWorldGenerationStore generationStore) {
        this.properties = properties;
        this.engine = engine;
        this.worldClock = worldClock;
        this.worldEventRepository = worldEventRepository;
        this.tickHistoryRepository = tickHistoryRepository;
        this.artifactRepository = artifactRepository;
        this.chronicleRepository = chronicleRepository;
        this.worldRepositoryProvider = worldRepositoryProvider;
        this.generationStore = generationStore;
    }

    public LivingWorldTickResult runManualTick() {
        if (!properties.isManualTickEnabled()) {
            throw new IllegalStateException("Manual world tick is disabled");
        }
        return runTick(WorldClockMode.MANUAL, Instant.now());
    }

    public List<LivingWorldTickResult> runScheduledTicks(Instant now) {
        List<LivingWorldTickResult> results = new ArrayList<>();
        for (WorldClockMode mode : worldClock.scheduledModesDue(now)) {
            results.add(runTick(mode, now));
        }
        return results;
    }

    public LivingWorldTickResult runTick(WorldClockMode mode, Instant now) {
        LivingWorldContext context = buildContext(mode, now);
        LivingWorldTickResult result = engine.tick(context);
        for (ConversationArtifact artifact : result.artifacts()) {
            if (artifactRepository.findById(artifact.id()).isEmpty()) {
                artifactRepository.save(artifact);
            }
        }
        persistTickHistory(result);
        generationStore.save(result);
        LOGGER.info(
                "Living world tick {} ({}) complete: events={}, artifacts={}, notifications={}",
                result.runId(),
                mode,
                result.eventsGenerated(),
                result.artifactsGenerated(),
                result.notificationsGenerated());
        return result;
    }

    public List<com.chugalkhorbandar.application.world.living.WorldEvent> listEvents() {
        return worldEventRepository.findAllOrderByCreatedAtDesc();
    }

    public com.chugalkhorbandar.application.world.living.WorldEvent getEvent(String id) {
        return worldEventRepository.findById(id).orElseThrow(WorldEventNotFoundException::new);
    }

    public List<com.chugalkhorbandar.application.world.living.WorldEvent> listEventsByType(WorldEventType type) {
        return worldEventRepository.findByTypeOrderByCreatedAtDesc(type);
    }

    public java.util.Optional<LivingWorldTickResult> getLatestTick() {
        return generationStore.getLatest();
    }

    public List<WorldTickHistory> getTickHistory() {
        return tickHistoryRepository.findAllOrderByStartedAtDesc();
    }

    private LivingWorldContext buildContext(WorldClockMode mode, Instant now) {
        List<String> characterIds = worldRepositoryProvider.characters().findAll(CharacterQuery.all()).stream()
                .map(character -> character.id())
                .toList();
        List<ConversationArtifact> allArtifacts = new ArrayList<>();
        List<ConversationArtifact> activeArtifacts = new ArrayList<>();
        for (String characterId : characterIds) {
            for (ConversationArtifact artifact : artifactRepository.findAllForCharacter(characterId)) {
                allArtifacts.add(artifact);
                if ((artifact.status() == ConversationArtifactStatus.ACTIVE
                                || artifact.status() == ConversationArtifactStatus.NEW)
                        && !artifact.expiresAt().isBefore(now)) {
                    activeArtifacts.add(artifact);
                }
            }
        }
        return new LivingWorldContext(
                now,
                worldClock.currentWorldDate(now),
                mode,
                characterIds,
                activeArtifacts,
                allArtifacts,
                chronicleRepository.findAllOrderByCreatedAtDesc(),
                List.of(),
                worldEventRepository.findAllEventIds());
    }

    private void persistTickHistory(LivingWorldTickResult result) {
        List<String> generators = result.trace().stream()
                .map(LivingWorldTraceEntry::generator)
                .distinct()
                .toList();
        tickHistoryRepository.save(new WorldTickHistory(
                result.runId(),
                result.mode(),
                result.startedAt(),
                result.completedAt(),
                result.durationMs(),
                result.worldDate(),
                result.eventsGenerated(),
                result.artifactsGenerated(),
                result.notificationsGenerated(),
                generators,
                result.events().stream().map(WorldEvent::id).toList(),
                result.artifactIds(),
                result.notificationIds()));
    }
}
