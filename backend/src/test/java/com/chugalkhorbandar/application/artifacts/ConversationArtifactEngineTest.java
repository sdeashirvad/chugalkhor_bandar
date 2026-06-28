package com.chugalkhorbandar.application.artifacts;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.conversation.director.ConversationArc;
import com.chugalkhorbandar.application.conversation.director.ConversationEnergy;
import com.chugalkhorbandar.application.conversation.director.ConversationGoal;
import com.chugalkhorbandar.application.conversation.director.ConversationOutcome;
import com.chugalkhorbandar.application.conversation.director.ConversationPlan;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanningTrace;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanningTraceEntry;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationArtifactEngineTest {

    private ConversationArtifactEngine engine;

    @BeforeEach
    void setUp() {
        ConversationArtifactProperties properties = new ConversationArtifactProperties();
        engine = new ConversationArtifactEngine(properties);
    }

    @Test
    void createsPromiseArtifactWhenOutcomeIsPromiseMade() {
        ConversationArtifactGenerationSnapshot snapshot = engine.generate(input(
                "remember this for me",
                executedSnapshot(ConversationOutcome.PROMISE_MADE, "remember")));

        assertThat(snapshot.generatedArtifacts()).hasSize(1);
        ConversationArtifact artifact = snapshot.generatedArtifacts().getFirst();
        assertThat(artifact.type()).isEqualTo(ConversationArtifactType.PROMISE);
        assertThat(artifact.ownerCharacterId()).isEqualTo(ConversationArtifactEngine.BANDAR_CHARACTER_ID);
        assertThat(artifact.recipientCharacterId()).isEqualTo("character_alpha");
        assertThat(artifact.createdByCharacterId()).isEqualTo("character_alpha");
    }

    @Test
    void createsReminderArtifactWhenUserAsksForReminder() {
        ConversationArtifactGenerationSnapshot snapshot = engine.generate(input(
                "remind me tomorrow",
                executedSnapshot(ConversationOutcome.PROMISE_MADE, "reminder")));

        assertThat(snapshot.generatedArtifacts()).hasSize(1);
        ConversationArtifact artifact = snapshot.generatedArtifacts().getFirst();
        assertThat(artifact.type()).isEqualTo(ConversationArtifactType.REMINDER);
        assertThat(artifact.ownerCharacterId()).isEqualTo("character_alpha");
        assertThat(artifact.recipientCharacterId()).isEqualTo(ConversationArtifactEngine.BANDAR_CHARACTER_ID);
    }

    @Test
    void createsStorySeedWhenStoryStarted() {
        ConversationArtifactGenerationSnapshot snapshot = engine.generate(input(
                "tell me a story",
                executedSnapshot(ConversationOutcome.STORY_STARTED, "story-request")));

        assertThat(snapshot.generatedArtifacts()).hasSize(1);
        assertThat(snapshot.generatedArtifacts().getFirst().type()).isEqualTo(ConversationArtifactType.STORY_SEED);
    }

    @Test
    void createsOpenQuestionWhenFollowUpRequired() {
        ConversationArtifactGenerationSnapshot snapshot = engine.generate(input(
                "hello",
                executedSnapshot(ConversationOutcome.FOLLOW_UP_REQUIRED, "greeting")));

        assertThat(snapshot.generatedArtifacts()).hasSize(1);
        assertThat(snapshot.generatedArtifacts().getFirst().type()).isEqualTo(ConversationArtifactType.OPEN_QUESTION);
    }

    @Test
    void producesNothingWhenNoRuleMatches() {
        ConversationArtifactGenerationSnapshot snapshot = engine.generate(input(
                "thanks",
                executedSnapshot(ConversationOutcome.RESOLVED, "goodbye")));

        assertThat(snapshot.generatedArtifacts()).isEmpty();
        assertThat(snapshot.trace()).extracting(ConversationArtifactGenerationTraceEntry::rule).contains("no-artifact");
    }

    @Test
    void skipsGenerationWhenExecutionIncomplete() {
        ConversationPlanSnapshot incomplete = ConversationPlanSnapshot.planned(
                "session-1",
                plan(ConversationOutcome.PROMISE_MADE),
                new ConversationPlanningTrace(List.of(new ConversationPlanningTraceEntry("remember", "test"))));

        ConversationArtifactGenerationSnapshot snapshot = engine.generate(input("remember this", incomplete));

        assertThat(snapshot.generatedArtifacts()).isEmpty();
    }

    @Test
    void ordersByPriority() {
        ConversationArtifactProperties properties = new ConversationArtifactProperties();
        ConversationArtifactEngine priorityEngine = new ConversationArtifactEngine(properties);
        ConversationArtifactGenerationSnapshot snapshot = priorityEngine.generate(input(
                "remember this",
                executedSnapshot(ConversationOutcome.PROMISE_MADE, "remember")));

        assertThat(snapshot.generatedArtifacts().getFirst().priority()).isEqualTo(ConversationArtifactPriority.HIGH);
    }

    private ConversationArtifactEngineInput input(String message, ConversationPlanSnapshot planSnapshot) {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        CurrentCharacter user = new CurrentCharacter("character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null, null);
        Conversation conversation = new Conversation(
                "conv-1",
                "session-1",
                new ConversationCharacter("character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null),
                now,
                now,
                ConversationStatus.ACTIVE,
                List.of());
        return new ConversationArtifactEngineInput(
                user, conversation, null, planSnapshot, null, null, message, now, List.of());
    }

    private static ConversationPlanSnapshot executedSnapshot(ConversationOutcome outcome, String rule) {
        ConversationPlanSnapshot planned = ConversationPlanSnapshot.planned(
                "session-1",
                plan(outcome),
                new ConversationPlanningTrace(List.of(new ConversationPlanningTraceEntry(rule, "test"))));
        return planned.withExecutionResult(
                plan(outcome), 1, 0, "", List.of(), List.of("msg-1"), List.of(), true);
    }

    private static ConversationPlan plan(ConversationOutcome outcome) {
        return new ConversationPlan(
                ConversationGoal.SMALL_TALK,
                0.8,
                true,
                ConversationEnergy.LOW,
                ConversationArc.SMALL_TALK,
                1,
                List.of(),
                false,
                false,
                false,
                false,
                false,
                "Warm",
                outcome,
                Instant.parse("2026-06-01T12:00:00Z"),
                false,
                false,
                null,
                null);
    }
}
