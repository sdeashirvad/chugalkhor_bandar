package com.chugalkhorbandar.application.behavior;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.conversation.director.ConversationArc;
import com.chugalkhorbandar.application.conversation.director.ConversationEnergy;
import com.chugalkhorbandar.application.conversation.director.ConversationGoal;
import com.chugalkhorbandar.application.conversation.director.ConversationOutcome;
import com.chugalkhorbandar.application.conversation.director.ConversationPlan;
import com.chugalkhorbandar.domain.conversation.ConversationWindow;
import com.chugalkhorbandar.domain.conversation.Sender;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BehaviorEngineTest {

    private BehaviorEngine engine;

    @BeforeEach
    void setUp() {
        engine = new BehaviorEngine();
    }

    @Test
    void storyBehaviorSelectsStoryNarrationAndLightHumor() {
        BehaviorProfileSnapshot snapshot = engine.select("session-1", input(ConversationGoal.STORY, ConversationArc.QUESTION_STORY, true));

        assertThat(snapshot.profile().narrationStyle()).isEqualTo(NarrationStyle.STORY);
        assertThat(snapshot.profile().humorLevel()).isEqualTo(HumorLevel.LIGHT);
        assertThat(snapshot.profile().storytellingPreference()).isEqualTo(StorytellingPreference.STRONG);
        assertThat(snapshot.profile().conversationFlavor())
                .isIn(ConversationFlavor.NOSTALGIC, ConversationFlavor.ADVENTUROUS, ConversationFlavor.CELEBRATORY);
    }

    @Test
    void locationBehaviorSelectsCalmDirectStyle() {
        BehaviorProfileSnapshot snapshot = engine.select("session-1", input(ConversationGoal.LOCATION_HELP, ConversationArc.QUESTION_ANSWER, false));

        assertThat(snapshot.profile().narrationStyle()).isEqualTo(NarrationStyle.DIRECT);
        assertThat(snapshot.profile().conversationFlavor()).isEqualTo(ConversationFlavor.CALM);
        assertThat(snapshot.profile().humorLevel()).isIn(HumorLevel.OFF, HumorLevel.LIGHT);
        assertThat(snapshot.profile().endingStyle()).isEqualTo(EndingStyle.NONE);
    }

    @Test
    void cheerUpBehaviorSelectsCozyPlayfulStyle() {
        BehaviorProfileSnapshot snapshot = engine.select("session-1", input(ConversationGoal.CHEER_UP, ConversationArc.CHEER_UP, false));

        assertThat(snapshot.profile().humorLevel()).isEqualTo(HumorLevel.MEDIUM);
        assertThat(snapshot.profile().conversationFlavor()).isEqualTo(ConversationFlavor.COZY);
        assertThat(snapshot.profile().endingStyle()).isEqualTo(EndingStyle.QUESTION);
        assertThat(snapshot.profile().openingStyle()).isIn(OpeningStyle.JOKE, OpeningStyle.OBSERVATION);
        assertThat(snapshot.profile().narrationStyle()).isEqualTo(NarrationStyle.PLAYFUL);
    }

    @Test
    void goodbyeBehaviorSelectsDirectCalmClose() {
        BehaviorProfileSnapshot snapshot = engine.select("session-1", input(ConversationGoal.GOODBYE, ConversationArc.GOODBYE, false));

        assertThat(snapshot.profile().narrationStyle()).isEqualTo(NarrationStyle.DIRECT);
        assertThat(snapshot.profile().endingStyle()).isEqualTo(EndingStyle.NONE);
        assertThat(snapshot.profile().humorLevel()).isEqualTo(HumorLevel.OFF);
        assertThat(snapshot.profile().conversationFlavor()).isEqualTo(ConversationFlavor.CALM);
    }

    @Test
    void flavorSelectionIsDeterministic() {
        BehaviorEngineInput input = input(ConversationGoal.STORY, ConversationArc.QUESTION_STORY, true);
        BehaviorProfile first = engine.select("session-1", input).profile();
        BehaviorProfile second = engine.select("session-1", input).profile();

        assertThat(first.conversationFlavor()).isEqualTo(second.conversationFlavor());
        assertThat(first.openingStyle()).isEqualTo(second.openingStyle());
        assertThat(first.humorLevel()).isEqualTo(second.humorLevel());
    }

    @Test
    void humorNeverExceedsMedium() {
        for (ConversationGoal goal : ConversationGoal.values()) {
            BehaviorProfile profile = engine.select("session-" + goal.name(), input(goal, ConversationArc.SMALL_TALK, false))
                    .profile();
            assertThat(profile.humorLevel()).isIn(HumorLevel.OFF, HumorLevel.LIGHT, HumorLevel.MEDIUM);
        }
    }

    @Test
    void traceRecordsAppliedRules() {
        BehaviorProfileSnapshot snapshot = engine.select("session-1", input(ConversationGoal.CHEER_UP, ConversationArc.CHEER_UP, false));

        assertThat(snapshot.trace().entries()).isNotEmpty();
        assertThat(snapshot.trace().entries()).extracting(BehaviorPlanningTraceEntry::rule).contains("cheer-up-behavior");
    }

    private static BehaviorEngineInput input(ConversationGoal goal, ConversationArc arc, boolean askFollowUp) {
        ConversationPlan plan = new ConversationPlan(
                goal,
                0.9,
                true,
                ConversationEnergy.HIGH,
                arc,
                2,
                List.of(0L),
                askFollowUp,
                goal == ConversationGoal.STORY,
                false,
                false,
                false,
                "Warm",
                ConversationOutcome.UNRESOLVED,
                Instant.parse("2026-01-01T00:00:00Z"),
                false,
                false,
                null,
                null);
        return new BehaviorEngineInput(
                null,
                null,
                plan,
                new ConversationWindow(List.of(), Sender.BANDAR, 10),
                new RuntimeWorldContext("READY", "1.0", 1, 2, List.of("Hippu King")),
                "Tell me a story");
    }
}
