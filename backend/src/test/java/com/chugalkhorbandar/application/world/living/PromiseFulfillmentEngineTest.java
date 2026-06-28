package com.chugalkhorbandar.application.world.living;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactPriority;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PromiseFulfillmentEngineTest {

    @Test
    void generatesPromiseDueEventWhenExpiresToday() {
        PromiseFulfillmentEngine engine = new PromiseFulfillmentEngine(enabledProperties());
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        LocalDate today = LocalDate.parse("2026-06-01");
        ConversationArtifact promise = new ConversationArtifact(
                "promise-1",
                ConversationArtifactType.PROMISE,
                "character_bandar",
                "character_alpha",
                "character_alpha",
                "conv-1",
                "Lost Crown",
                "Tell the Lost Crown story",
                ConversationArtifactStatus.ACTIVE,
                ConversationArtifactPriority.HIGH,
                now.minus(30, ChronoUnit.DAYS),
                now,
                now,
                Map.of(),
                List.of());

        LivingWorldGeneratorResult result = engine.generate(new LivingWorldContext(
                now, today, WorldClockMode.MANUAL, List.of(), List.of(promise), List.of(promise), List.of(), List.of(), Set.of()));

        assertThat(result.events()).hasSize(1);
        assertThat(result.events().get(0).type()).isEqualTo(WorldEventType.PROMISE_DUE);
    }

    private static LivingWorldProperties enabledProperties() {
        LivingWorldProperties properties = new LivingWorldProperties();
        properties.setPromiseEngineEnabled(true);
        return properties;
    }
}
