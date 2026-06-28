package com.chugalkhorbandar.application.artifacts;

import com.chugalkhorbandar.application.conversation.director.ConversationOutcome;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanningTraceEntry;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ConversationArtifactEngine {

    public static final String BANDAR_CHARACTER_ID = "character_bandar";

    private final ConversationArtifactProperties properties;

    public ConversationArtifactEngine(ConversationArtifactProperties properties) {
        this.properties = properties;
    }

    public ConversationArtifactGenerationSnapshot generate(ConversationArtifactEngineInput input) {
        List<ConversationArtifactGenerationTraceEntry> trace = new ArrayList<>();
        List<ConversationArtifact> generated = new ArrayList<>();
        CurrentCharacter user = input.currentUser();
        String conversationId = input.conversation().conversationId();
        ConversationPlanSnapshot planSnapshot = input.planSnapshot();
        ConversationOutcome outcome = planSnapshot.plan().outcome();
        String matchedRule = matchedRuleName(planSnapshot);
        String normalizedMessage = normalize(input.latestUserMessage());

        if (!planSnapshot.executed()) {
            trace.add(new ConversationArtifactGenerationTraceEntry(
                    "execution-incomplete", "Conversation turn did not complete; skipping artifact generation"));
            return snapshot(user.id(), conversationId, input.currentTime(), trace, generated);
        }

        if (matchesReminderRequest(normalizedMessage, matchedRule)) {
            trace.add(new ConversationArtifactGenerationTraceEntry(
                    "reminder-rule", "User explicitly asked for a reminder"));
            generated.add(buildArtifact(
                    ConversationArtifactType.REMINDER,
                    user.id(),
                    BANDAR_CHARACTER_ID,
                    user.id(),
                    conversationId,
                    "Reminder request",
                    "User asked Bandar for a reminder.",
                    ConversationArtifactPriority.MEDIUM,
                    "reminder-request",
                    outcome,
                    matchedRule,
                    input.currentTime()));
        } else if (outcome == ConversationOutcome.PROMISE_MADE) {
            trace.add(new ConversationArtifactGenerationTraceEntry(
                    "promise-rule", "Conversation outcome is PROMISE_MADE"));
            generated.add(buildArtifact(
                    ConversationArtifactType.PROMISE,
                    BANDAR_CHARACTER_ID,
                    user.id(),
                    user.id(),
                    conversationId,
                    "Promise",
                    "Bandar made a promise during conversation.",
                    ConversationArtifactPriority.HIGH,
                    "promise-made",
                    outcome,
                    matchedRule,
                    input.currentTime()));
        } else if (outcome == ConversationOutcome.STORY_STARTED) {
            trace.add(new ConversationArtifactGenerationTraceEntry(
                    "story-seed-rule", "Conversation outcome is STORY_STARTED"));
            generated.add(buildArtifact(
                    ConversationArtifactType.STORY_SEED,
                    BANDAR_CHARACTER_ID,
                    user.id(),
                    BANDAR_CHARACTER_ID,
                    conversationId,
                    "Story in progress",
                    "A story was started and remains unfinished.",
                    ConversationArtifactPriority.MEDIUM,
                    "story-started",
                    outcome,
                    matchedRule,
                    input.currentTime()));
        } else if (outcome == ConversationOutcome.FOLLOW_UP_REQUIRED) {
            trace.add(new ConversationArtifactGenerationTraceEntry(
                    "follow-up-rule", "Conversation outcome is FOLLOW_UP_REQUIRED"));
            generated.add(buildArtifact(
                    ConversationArtifactType.OPEN_QUESTION,
                    BANDAR_CHARACTER_ID,
                    user.id(),
                    BANDAR_CHARACTER_ID,
                    conversationId,
                    "Open follow-up",
                    "Conversation requires follow-up attention.",
                    ConversationArtifactPriority.LOW,
                    "follow-up-required",
                    outcome,
                    matchedRule,
                    input.currentTime()));
        } else {
            trace.add(new ConversationArtifactGenerationTraceEntry(
                    "no-artifact", "No artifact rules matched; producing nothing"));
        }

        return snapshot(user.id(), conversationId, input.currentTime(), trace, generated);
    }

    private ConversationArtifact buildArtifact(
            ConversationArtifactType type,
            String ownerCharacterId,
            String recipientCharacterId,
            String createdByCharacterId,
            String conversationId,
            String title,
            String summary,
            ConversationArtifactPriority priority,
            String trigger,
            ConversationOutcome outcome,
            String matchedRule,
            Instant now) {
        Instant expiresAt = now.plus(properties.getArtifactExpirationDays(), ChronoUnit.DAYS);
        return new ConversationArtifact(
                UUID.randomUUID().toString(),
                type,
                ownerCharacterId,
                recipientCharacterId,
                createdByCharacterId,
                conversationId,
                title,
                summary,
                ConversationArtifactStatus.NEW,
                priority,
                now,
                now,
                expiresAt,
                Map.of(
                        "outcome", outcome.name(),
                        "matchedRule", matchedRule == null ? "" : matchedRule,
                        "trigger", trigger),
                List.of("created:" + trigger));
    }

    private static boolean matchesReminderRequest(String normalizedMessage, String matchedRule) {
        return normalizedMessage.contains("remind me") || "reminder".equals(matchedRule);
    }

    private static String matchedRuleName(ConversationPlanSnapshot planSnapshot) {
        if (planSnapshot.trace() == null || planSnapshot.trace().entries().isEmpty()) {
            return "";
        }
        ConversationPlanningTraceEntry entry = planSnapshot.trace().entries().getFirst();
        return entry.rule() == null ? "" : entry.rule();
    }

    private static String normalize(String message) {
        return message == null ? "" : message.toLowerCase(Locale.ROOT).trim();
    }

    private static ConversationArtifactGenerationSnapshot snapshot(
            String characterId,
            String conversationId,
            Instant generatedAt,
            List<ConversationArtifactGenerationTraceEntry> trace,
            List<ConversationArtifact> generated) {
        return new ConversationArtifactGenerationSnapshot(characterId, conversationId, generatedAt, trace, generated);
    }
}
