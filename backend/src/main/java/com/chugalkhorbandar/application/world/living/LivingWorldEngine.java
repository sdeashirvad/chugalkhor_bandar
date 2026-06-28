package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.domain.world.living.ports.WorldEventRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LivingWorldEngine {

    private final LivingWorldProperties properties;
    private final FestivalEngine festivalEngine;
    private final BirthdayEngine birthdayEngine;
    private final PromiseFulfillmentEngine promiseFulfillmentEngine;
    private final CharacterInitiativeEngine characterInitiativeEngine;
    private final GossipEngine gossipEngine;
    private final LivingWorldArtifactFactory artifactFactory;
    private final LivingWorldNotificationBridge notificationBridge;
    private final WorldEventRepository worldEventRepository;

    public LivingWorldEngine(
            LivingWorldProperties properties,
            FestivalEngine festivalEngine,
            BirthdayEngine birthdayEngine,
            PromiseFulfillmentEngine promiseFulfillmentEngine,
            CharacterInitiativeEngine characterInitiativeEngine,
            GossipEngine gossipEngine,
            LivingWorldArtifactFactory artifactFactory,
            LivingWorldNotificationBridge notificationBridge,
            WorldEventRepository worldEventRepository) {
        this.properties = properties;
        this.festivalEngine = festivalEngine;
        this.birthdayEngine = birthdayEngine;
        this.promiseFulfillmentEngine = promiseFulfillmentEngine;
        this.characterInitiativeEngine = characterInitiativeEngine;
        this.gossipEngine = gossipEngine;
        this.artifactFactory = artifactFactory;
        this.notificationBridge = notificationBridge;
        this.worldEventRepository = worldEventRepository;
    }

    public LivingWorldTickResult tick(LivingWorldContext baseContext) {
        if (!properties.isEnabled()) {
            throw new IllegalStateException("Living world engine is disabled");
        }
        Instant startedAt = baseContext.now();
        String runId = UUID.randomUUID().toString();
        List<LivingWorldTraceEntry> trace = new ArrayList<>();
        Set<String> knownEventIds = new HashSet<>(baseContext.existingEventIds());

        LivingWorldGeneratorResult phaseOne = festivalEngine.generate(baseContext)
                .merge(birthdayEngine.generate(baseContext))
                .merge(promiseFulfillmentEngine.generate(baseContext))
                .merge(characterInitiativeEngine.generate(baseContext));

        List<WorldEvent> events = new ArrayList<>(phaseOne.events());
        trace.addAll(phaseOne.trace());
        events.forEach(event -> knownEventIds.add(event.id()));

        LivingWorldContext gossipContext = new LivingWorldContext(
                baseContext.now(),
                baseContext.today(),
                baseContext.mode(),
                baseContext.allCharacterIds(),
                baseContext.activeArtifacts(),
                baseContext.allArtifacts(),
                baseContext.chronicles(),
                List.copyOf(events),
                knownEventIds);

        LivingWorldGeneratorResult gossipResult = gossipEngine.generate(gossipContext);
        events.addAll(gossipResult.events());
        trace.addAll(gossipResult.trace());

        List<WorldEvent> persistedEvents = new ArrayList<>();
        for (WorldEvent event : events) {
            if (!baseContext.existingEventIds().contains(event.id())) {
                persistedEvents.add(worldEventRepository.save(event));
            }
        }

        List<ConversationArtifact> artifacts = artifactFactory.createFromEvents(persistedEvents, startedAt);
        List<com.chugalkhorbandar.application.notification.Notification> notifications =
                notificationBridge.deliverFromWorldTick(persistedEvents, artifacts, startedAt);

        Instant completedAt = Instant.now();
        return new LivingWorldTickResult(
                runId,
                baseContext.mode(),
                startedAt,
                completedAt,
                completedAt.toEpochMilli() - startedAt.toEpochMilli(),
                baseContext.today(),
                persistedEvents.size(),
                artifacts.size(),
                notifications.size(),
                persistedEvents,
                artifacts,
                artifacts.stream().map(ConversationArtifact::id).toList(),
                notifications.stream().map(com.chugalkhorbandar.application.notification.Notification::id).toList(),
                trace);
    }
}
