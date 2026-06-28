package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.chronicle.Chronicle;
import com.chugalkhorbandar.application.chronicle.ChronicleCategory;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CharacterInitiativeEngine {

    private static final String RABBITU_MINISTER = "character_rabbitu_minister";
    private static final String SECOND_HIPPU = "character_second_hippu";
    private static final String LITTLE_BROTHER = "character_little_brother";
    private static final String BANDAR = "character_bandar";

    private final LivingWorldProperties properties;

    public CharacterInitiativeEngine(LivingWorldProperties properties) {
        this.properties = properties;
    }

    public LivingWorldGeneratorResult generate(LivingWorldContext context) {
        if (!properties.isCharacterInitiativeEnabled()) {
            return LivingWorldGeneratorResult.empty();
        }
        List<WorldEvent> events = new ArrayList<>();
        List<LivingWorldTraceEntry> trace = new ArrayList<>();

        if (rabbituMeetingEligible(context)) {
            addEvent(
                    events,
                    trace,
                    context,
                    RABBITU_MINISTER,
                    "Rabbitu Minister requests a meeting",
                    "An important Rabbitu chronicle warrants a royal meeting.",
                    "rabbitu-meeting-request");
        }
        if (secondHippuAdventureEligible(context)) {
            addEvent(
                    events,
                    trace,
                    context,
                    SECOND_HIPPU,
                    "Second Hippu begins a new adventure",
                    "Second Hippu completed a previous adventure and seeks a new quest.",
                    "second-hippu-adventure");
        }
        if (littleBrotherStoryEligible(context)) {
            addEvent(
                    events,
                    trace,
                    context,
                    LITTLE_BROTHER,
                    "Little Brother asks about a story",
                    "Little Brother wishes to hear more about a previous story.",
                    "little-brother-story-question");
        }
        if (bandarStoryEligible(context)) {
            addEvent(
                    events,
                    trace,
                    context,
                    BANDAR,
                    "Bandar continues an unfinished story",
                    "Bandar decides to continue a story left unfinished.",
                    "bandar-continue-story");
        }
        if (events.isEmpty()) {
            trace.add(new LivingWorldTraceEntry(
                    "CharacterInitiativeEngine", "no-initiative", "No character initiative rules matched"));
        }
        return new LivingWorldGeneratorResult(events, trace);
    }

    private static boolean rabbituMeetingEligible(LivingWorldContext context) {
        return context.chronicles().stream().anyMatch(chronicle -> chronicle.category() == ChronicleCategory.WORLD
                || chronicle.ownerCharacterId().contains("rabbitu")
                || chronicle.summary().toLowerCase().contains("rabbitu"));
    }

    private static boolean secondHippuAdventureEligible(LivingWorldContext context) {
        boolean completedStory = context.allArtifacts().stream()
                .anyMatch(artifact -> SECOND_HIPPU.equals(artifact.ownerCharacterId())
                        && artifact.type() == ConversationArtifactType.STORY_SEED
                        && artifact.status() == ConversationArtifactStatus.FULFILLED);
        boolean activeStory = context.activeArtifacts().stream()
                .anyMatch(artifact -> SECOND_HIPPU.equals(artifact.ownerCharacterId())
                        && artifact.type() == ConversationArtifactType.STORY_SEED
                        && (artifact.status() == ConversationArtifactStatus.ACTIVE
                                || artifact.status() == ConversationArtifactStatus.NEW));
        return completedStory && !activeStory;
    }

    private static boolean littleBrotherStoryEligible(LivingWorldContext context) {
        return context.chronicles().stream()
                .anyMatch(chronicle -> LITTLE_BROTHER.equals(chronicle.ownerCharacterId())
                        && chronicle.category() == ChronicleCategory.STORY);
    }

    private static boolean bandarStoryEligible(LivingWorldContext context) {
        return context.activeArtifacts().stream()
                .anyMatch(artifact -> BANDAR.equals(artifact.ownerCharacterId())
                        && artifact.type() == ConversationArtifactType.STORY_SEED
                        && (artifact.status() == ConversationArtifactStatus.ACTIVE
                                || artifact.status() == ConversationArtifactStatus.NEW));
    }

    private static void addEvent(
            List<WorldEvent> events,
            List<LivingWorldTraceEntry> trace,
            LivingWorldContext context,
            String characterId,
            String title,
            String summary,
            String rule) {
        String eventId = WorldEventIdFactory.create(WorldEventType.CHARACTER_ACTIVITY, context.today(), characterId + "-" + rule);
        if (context.existingEventIds().contains(eventId)) {
            trace.add(new LivingWorldTraceEntry("CharacterInitiativeEngine", "skip-duplicate", eventId));
            return;
        }
        events.add(new WorldEvent(
                eventId,
                WorldEventType.CHARACTER_ACTIVITY,
                title,
                summary,
                List.of(characterId),
                WorldEventVisibility.PRIVATE,
                context.now(),
                context.today(),
                java.util.Map.of("characterId", characterId, "rule", rule),
                WorldEventStatus.ACTIVE,
                WorldEventOrigin.CHARACTER_INITIATIVE_ENGINE));
        trace.add(new LivingWorldTraceEntry("CharacterInitiativeEngine", rule, title));
    }
}
