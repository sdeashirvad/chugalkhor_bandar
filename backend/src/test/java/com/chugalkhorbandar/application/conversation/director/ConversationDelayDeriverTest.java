package com.chugalkhorbandar.application.conversation.director;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationDelayDeriverTest {

    private ConversationDirectorProperties properties;

    @BeforeEach
    void setUp() {
        properties = new ConversationDirectorProperties();
        properties.getMessageDelayRange().setSecondMessageMinMs(2000);
        properties.getMessageDelayRange().setSecondMessageMaxMs(4000);
        properties.getMessageDelayRange().setThirdMessageMinMs(3000);
        properties.getMessageDelayRange().setThirdMessageMaxMs(6000);
        properties.setDevDelayMultiplier(1.0);
    }

    @Test
    void singleMessagePlanHasNoDelays() {
        List<Long> delays = ConversationDelayDeriver.derive(1, "session-1", "Hello", properties);
        assertThat(delays).isEmpty();
    }

    @Test
    void twoMessagePlanHasOneDelayInRange() {
        List<Long> delays = ConversationDelayDeriver.derive(2, "session-1", "Hello", properties);
        assertThat(delays).hasSize(1);
        assertThat(delays.get(0)).isBetween(2000L, 4000L);
    }

    @Test
    void threeMessagePlanHasTwoDelaysInRange() {
        List<Long> delays = ConversationDelayDeriver.derive(3, "session-1", "Tell me a story", properties);
        assertThat(delays).hasSize(2);
        assertThat(delays.get(0)).isBetween(2000L, 4000L);
        assertThat(delays.get(1)).isBetween(3000L, 6000L);
    }

    @Test
    void developerMultiplierScalesDelays() {
        properties.setDevDelayMultiplier(0.25);
        List<Long> delays = ConversationDelayDeriver.derive(3, "session-1", "Tell me a story", properties);
        assertThat(delays.get(0)).isBetween(500L, 1000L);
        assertThat(delays.get(1)).isBetween(750L, 1500L);
    }

    @Test
    void delaysAreDeterministic() {
        List<Long> first = ConversationDelayDeriver.derive(3, "session-1", "Tell me a story", properties);
        List<Long> second = ConversationDelayDeriver.derive(3, "session-1", "Tell me a story", properties);
        assertThat(first).isEqualTo(second);
    }
}
