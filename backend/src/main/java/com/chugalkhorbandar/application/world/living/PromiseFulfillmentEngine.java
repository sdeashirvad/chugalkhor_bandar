package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PromiseFulfillmentEngine {

    private static final String BANDAR_ID = "character_bandar";

    private final LivingWorldProperties properties;

    public PromiseFulfillmentEngine(LivingWorldProperties properties) {
        this.properties = properties;
    }

    public LivingWorldGeneratorResult generate(LivingWorldContext context) {
        if (!properties.isPromiseEngineEnabled()) {
            return LivingWorldGeneratorResult.empty();
        }
        List<WorldEvent> events = new ArrayList<>();
        List<LivingWorldTraceEntry> trace = new ArrayList<>();
        for (ConversationArtifact artifact : context.activeArtifacts()) {
            if (artifact.type() != ConversationArtifactType.PROMISE) {
                continue;
            }
            if (artifact.status() != ConversationArtifactStatus.ACTIVE
                    && artifact.status() != ConversationArtifactStatus.NEW) {
                continue;
            }
            if (!isDueToday(artifact, context.today())) {
                continue;
            }
            String eventId = WorldEventIdFactory.create(WorldEventType.PROMISE_DUE, context.today(), artifact.id());
            if (context.existingEventIds().contains(eventId)) {
                trace.add(new LivingWorldTraceEntry("PromiseFulfillmentEngine", "skip-duplicate", eventId));
                continue;
            }
            events.add(new WorldEvent(
                    eventId,
                    WorldEventType.PROMISE_DUE,
                    "Promise Due: " + artifact.title(),
                    "Bandar remembers a promise: " + artifact.summary(),
                    List.of(BANDAR_ID, artifact.recipientCharacterId()),
                    WorldEventVisibility.PRIVATE,
                    context.now(),
                    context.today(),
                    java.util.Map.of(
                            "artifactId", artifact.id(),
                            "recipientCharacterId", artifact.recipientCharacterId()),
                    WorldEventStatus.ACTIVE,
                    WorldEventOrigin.PROMISE_ENGINE));
            trace.add(new LivingWorldTraceEntry(
                    "PromiseFulfillmentEngine", "promise-due", artifact.id()));
        }
        if (events.isEmpty()) {
            trace.add(new LivingWorldTraceEntry("PromiseFulfillmentEngine", "no-promise-due", "No promises due today"));
        }
        return new LivingWorldGeneratorResult(events, trace);
    }

    private static boolean isDueToday(ConversationArtifact artifact, LocalDate today) {
        String dueDate = artifact.metadata().get("dueDate");
        if (dueDate != null && !dueDate.isBlank()) {
            try {
                return LocalDate.parse(dueDate).equals(today);
            } catch (Exception exception) {
                return false;
            }
        }
        LocalDate expiresDate = artifact.expiresAt().atZone(ZoneId.systemDefault()).toLocalDate();
        return expiresDate.equals(today);
    }
}
