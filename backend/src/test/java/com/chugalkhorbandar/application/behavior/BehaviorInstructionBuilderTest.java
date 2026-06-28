package com.chugalkhorbandar.application.behavior;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class BehaviorInstructionBuilderTest {

    @Test
    void instructionsUseNaturalLanguageWithoutEnumNames() {
        String instruction = BehaviorInstructionBuilder.build(new BehaviorProfile(
                OpeningStyle.OBSERVATION,
                NarrationStyle.HISTORICAL,
                HumorLevel.LIGHT,
                CuriosityLevel.HIGH,
                EndingStyle.QUESTION,
                ConversationFlavor.CALM,
                EnergyModifier.STEADY,
                StorytellingPreference.BALANCED,
                Instant.now()));

        assertThat(instruction).contains("Today you feel");
        assertThat(instruction).contains("gentle observation");
        assertThat(instruction).contains("historical storytelling");
        assertThat(instruction).contains("light humor");
        assertThat(instruction).contains("reflective pause");
        assertThat(instruction).contains("gentle curiosity");
        assertThat(instruction.toUpperCase()).doesNotContain("OPENINGSTYLE");
        assertThat(instruction.toUpperCase()).doesNotContain("NARRATIONSTYLE");
        assertThat(instruction.toUpperCase()).doesNotContain("HUMORLEVEL");
    }

    @Test
    void goodbyeInstructionsAvoidHumorAndExtension() {
        String instruction = BehaviorInstructionBuilder.build(new BehaviorProfile(
                OpeningStyle.DIRECT,
                NarrationStyle.DIRECT,
                HumorLevel.OFF,
                CuriosityLevel.LOW,
                EndingStyle.NONE,
                ConversationFlavor.CALM,
                EnergyModifier.SUBDUED,
                StorytellingPreference.MINIMAL,
                Instant.now()));

        assertThat(instruction).contains("Avoid humor");
        assertThat(instruction).contains("without extending the conversation");
    }
}
