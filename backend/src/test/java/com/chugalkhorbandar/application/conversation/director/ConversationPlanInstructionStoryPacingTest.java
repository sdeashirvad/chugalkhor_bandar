package com.chugalkhorbandar.application.conversation.director;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConversationPlanInstructionStoryPacingTest {

    @Test
    void threePartStoryArcUsesNaturalBoundaries() {
        ConversationPlan plan = new ConversationPlan(
                ConversationGoal.STORY,
                0.94,
                true,
                ConversationEnergy.VERY_HIGH,
                ConversationArc.QUESTION_STORY,
                3,
                List.of(2000L, 3000L),
                true,
                true,
                false,
                false,
                false,
                "Narrative",
                ConversationOutcome.STORY_STARTED,
                Instant.now(),
                false,
                false,
                null,
                null);

        String intro = ConversationPlanInstructionBuilder.build(plan, 0, 3);
        String main = ConversationPlanInstructionBuilder.build(plan, 1, 3);
        String close = ConversationPlanInstructionBuilder.build(plan, 2, 3);

        assertThat(intro).contains("brief introduction");
        assertThat(main).contains("main story body");
        assertThat(close).contains("reflection");
    }

    @Test
    void goodbyeArcNeverSuggestsFollowUpExtension() {
        ConversationPlan plan = new ConversationPlan(
                ConversationGoal.GOODBYE,
                0.95,
                false,
                ConversationEnergy.LOW,
                ConversationArc.GOODBYE,
                1,
                List.of(),
                false,
                false,
                false,
                false,
                true,
                "Warm",
                ConversationOutcome.RESOLVED,
                Instant.now(),
                false,
                false,
                null,
                null);

        String instruction = ConversationPlanInstructionBuilder.build(plan, 0, 1);
        assertThat(instruction).contains("Do not extend the conversation");
        assertThat(instruction).doesNotContain("follow-up question");
    }
}
