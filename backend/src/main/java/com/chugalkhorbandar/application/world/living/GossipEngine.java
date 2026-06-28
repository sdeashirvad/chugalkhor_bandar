package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.chronicle.Chronicle;
import com.chugalkhorbandar.application.chronicle.ChronicleVisibility;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class GossipEngine {

    private static final String BANDAR = "character_bandar";

    private final LivingWorldProperties properties;

    public GossipEngine(LivingWorldProperties properties) {
        this.properties = properties;
    }

    public LivingWorldGeneratorResult generate(LivingWorldContext context) {
        if (!properties.isGossipEnabled()) {
            return LivingWorldGeneratorResult.empty();
        }
        List<WorldEvent> events = new ArrayList<>();
        List<LivingWorldTraceEntry> trace = new ArrayList<>();
        Set<String> gossipKeys = new HashSet<>();

        for (Chronicle chronicle : context.chronicles()) {
            if (chronicle.visibility() != ChronicleVisibility.PUBLIC) {
                continue;
            }
            String key = "chronicle-" + chronicle.id();
            if (!gossipKeys.add(key)) {
                continue;
            }
            addGossipEvent(events, trace, context, key, "Chronicle whispers: " + chronicle.title(), chronicle.summary());
        }

        for (ConversationArtifact artifact : context.activeArtifacts()) {
            if (artifact.type() == ConversationArtifactType.GOSSIP
                    || artifact.type() == ConversationArtifactType.RUMOR) {
                continue;
            }
            if (artifact.status() != ConversationArtifactStatus.ACTIVE
                    && artifact.status() != ConversationArtifactStatus.NEW) {
                continue;
            }
            String key = "artifact-" + artifact.id();
            if (!gossipKeys.add(key)) {
                continue;
            }
            addGossipEvent(events, trace, context, key, "Jungle rumor: " + artifact.title(), artifact.summary());
        }

        for (WorldEvent sourceEvent : context.generatedEvents()) {
            if (sourceEvent.type() != WorldEventType.FESTIVAL && sourceEvent.type() != WorldEventType.BIRTHDAY) {
                continue;
            }
            String key = "event-" + sourceEvent.id();
            if (!gossipKeys.add(key)) {
                continue;
            }
            addGossipEvent(events, trace, context, key, "Word spreads: " + sourceEvent.title(), sourceEvent.summary());
        }

        if (events.isEmpty()) {
            trace.add(new LivingWorldTraceEntry("GossipEngine", "no-gossip", "No gossip sources matched"));
        }
        return new LivingWorldGeneratorResult(events, trace);
    }

    private static void addGossipEvent(
            List<WorldEvent> events,
            List<LivingWorldTraceEntry> trace,
            LivingWorldContext context,
            String key,
            String title,
            String summary) {
        String eventId = WorldEventIdFactory.create(WorldEventType.ANNOUNCEMENT, context.today(), "gossip-" + key);
        if (context.existingEventIds().contains(eventId)) {
            trace.add(new LivingWorldTraceEntry("GossipEngine", "skip-duplicate", eventId));
            return;
        }
        events.add(new WorldEvent(
                eventId,
                WorldEventType.ANNOUNCEMENT,
                title,
                summary,
                List.of(BANDAR),
                WorldEventVisibility.PUBLIC,
                context.now(),
                context.today(),
                java.util.Map.of("gossipSource", key),
                WorldEventStatus.ACTIVE,
                WorldEventOrigin.GOSSIP_ENGINE));
        trace.add(new LivingWorldTraceEntry("GossipEngine", "gossip-generated", key));
    }
}
