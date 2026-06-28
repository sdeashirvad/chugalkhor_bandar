package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactPriority;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactEngine;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LivingWorldArtifactFactory {

    private static final int ARTIFACT_EXPIRY_DAYS = 30;

    public List<ConversationArtifact> createFromEvents(List<WorldEvent> events, Instant now) {
        List<ConversationArtifact> artifacts = new ArrayList<>();
        for (WorldEvent event : events) {
            artifacts.add(fromEvent(event, now));
        }
        return artifacts;
    }

    private static ConversationArtifact fromEvent(WorldEvent event, Instant now) {
        ConversationArtifactType type = mapType(event.type());
        String owner = event.participants().isEmpty()
                ? ConversationArtifactEngine.BANDAR_CHARACTER_ID
                : event.participants().getFirst();
        String recipient = event.participants().size() > 1
                ? event.participants().get(1)
                : owner;
        if (event.type() == WorldEventType.ANNOUNCEMENT && event.origin() == WorldEventOrigin.GOSSIP_ENGINE) {
            type = ConversationArtifactType.GOSSIP;
        }
        Instant expiresAt = now.plus(ARTIFACT_EXPIRY_DAYS, ChronoUnit.DAYS);
        return new ConversationArtifact(
                "art-world-" + event.id(),
                type,
                ConversationArtifactEngine.BANDAR_CHARACTER_ID,
                recipient,
                owner,
                "",
                event.title(),
                event.summary(),
                ConversationArtifactStatus.ACTIVE,
                priorityFor(event.type()),
                now,
                now,
                expiresAt,
                Map.of(
                        "worldEventId", event.id(),
                        "worldEventType", event.type().name(),
                        "origin", event.origin().name(),
                        "trigger", "living-world-engine"),
                List.of("created:living-world-tick"));
    }

    private static ConversationArtifactType mapType(WorldEventType eventType) {
        return switch (eventType) {
            case PROMISE_DUE -> ConversationArtifactType.REMINDER;
            case FESTIVAL, BIRTHDAY -> ConversationArtifactType.INVITATION;
            case CHARACTER_ACTIVITY -> ConversationArtifactType.TASK;
            case ANNOUNCEMENT -> ConversationArtifactType.GOSSIP;
            case DISCOVERY -> ConversationArtifactType.STORY_SEED;
            default -> ConversationArtifactType.DELIVERY;
        };
    }

    private static ConversationArtifactPriority priorityFor(WorldEventType eventType) {
        return switch (eventType) {
            case PROMISE_DUE, BIRTHDAY -> ConversationArtifactPriority.HIGH;
            case FESTIVAL, CHARACTER_ACTIVITY -> ConversationArtifactPriority.MEDIUM;
            default -> ConversationArtifactPriority.LOW;
        };
    }
}
