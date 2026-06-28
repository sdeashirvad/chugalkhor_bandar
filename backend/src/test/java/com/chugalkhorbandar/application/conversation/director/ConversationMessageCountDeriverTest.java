package com.chugalkhorbandar.application.conversation.director;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationMessageCountDeriverTest {

    private ConversationDirectorProperties properties;

    @BeforeEach
    void setUp() {
        properties = new ConversationDirectorProperties();
        properties.setMaxMessages(3);
    }

    @Test
    void lowEnergyAlwaysProducesOneMessage() {
        for (int index = 0; index < 20; index++) {
            int count = ConversationMessageCountDeriver.derive(
                    ConversationEnergy.LOW, "session-" + index, "message-" + index, properties);
            assertThat(count).isEqualTo(1);
        }
    }

    @Test
    void highEnergyProducesMultipleMessages() {
        boolean sawTwo = false;
        boolean sawThree = false;
        for (int index = 0; index < 50; index++) {
            int count = ConversationMessageCountDeriver.derive(
                    ConversationEnergy.HIGH, "session-high", "message-" + index, properties);
            assertThat(count).isBetween(2, 3);
            if (count == 2) {
                sawTwo = true;
            }
            if (count == 3) {
                sawThree = true;
            }
        }
        assertThat(sawTwo).isTrue();
        assertThat(sawThree).isTrue();
    }

    @Test
    void veryHighEnergyNeverExceedsMaxMessages() {
        properties.setMaxMessages(3);
        int count = ConversationMessageCountDeriver.derive(
                ConversationEnergy.VERY_HIGH, "session-1", "Tell me everything", properties);
        assertThat(count).isEqualTo(3);
    }

    @Test
    void derivedCountIsDeterministic() {
        int first = ConversationMessageCountDeriver.derive(
                ConversationEnergy.MEDIUM, "session-1", "Where am I?", properties);
        int second = ConversationMessageCountDeriver.derive(
                ConversationEnergy.MEDIUM, "session-1", "Where am I?", properties);
        assertThat(first).isEqualTo(second);
    }

    @Test
    void mediumEnergyProducesOneOrTwoMessages() {
        List<Integer> counts = java.util.stream.IntStream.range(0, 30)
                .mapToObj(index -> ConversationMessageCountDeriver.derive(
                        ConversationEnergy.MEDIUM, "session-medium", "msg-" + index, properties))
                .distinct()
                .toList();
        assertThat(counts).containsOnly(1, 2);
    }
}
