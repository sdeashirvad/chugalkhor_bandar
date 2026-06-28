package com.chugalkhorbandar.application.world.living;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.chronicle.Chronicle;
import com.chugalkhorbandar.application.chronicle.ChronicleCategory;
import com.chugalkhorbandar.application.chronicle.ChronicleConfidence;
import com.chugalkhorbandar.application.chronicle.ChronicleProvenance;
import com.chugalkhorbandar.application.chronicle.ChronicleVisibility;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GossipEngineTest {

    @Test
    void generatesGossipFromPublicChronicle() {
        GossipEngine engine = new GossipEngine(enabledProperties());
        Chronicle chronicle = new Chronicle(
                "chron-public-1",
                "Royal Decree",
                ChronicleCategory.WORLD,
                ChronicleVisibility.PUBLIC,
                ChronicleConfidence.OFFICIAL,
                "character_bandar",
                "Something happened in the Jungle",
                "Something happened in the Jungle",
                Instant.parse("2026-06-27T12:00:00Z"),
                LocalDate.parse("2026-06-27"),
                Map.of(),
                new ChronicleProvenance("", List.of(), List.of(), List.of(), "", "", "chron-public-1", List.of(), Map.of()),
                1);

        LivingWorldGeneratorResult result = engine.generate(new LivingWorldContext(
                Instant.parse("2026-06-27T12:00:00Z"),
                LocalDate.parse("2026-06-27"),
                WorldClockMode.DAILY,
                List.of(),
                List.of(),
                List.of(),
                List.of(chronicle),
                List.of(),
                Set.of()));

        assertThat(result.events()).hasSize(1);
        assertThat(result.events().get(0).type()).isEqualTo(WorldEventType.ANNOUNCEMENT);
        assertThat(result.events().get(0).title()).contains("Royal Decree");
    }

    @Test
    void generatesGossipFromFestivalEvent() {
        GossipEngine engine = new GossipEngine(enabledProperties());
        WorldEvent festival = new WorldEvent(
                "evt-festival",
                WorldEventType.FESTIVAL,
                "Spring Festival",
                "Celebration today",
                List.of(),
                WorldEventVisibility.PUBLIC,
                Instant.parse("2026-06-27T12:00:00Z"),
                LocalDate.parse("2026-06-27"),
                Map.of(),
                WorldEventStatus.ACTIVE,
                WorldEventOrigin.FESTIVAL_ENGINE);

        LivingWorldGeneratorResult result = engine.generate(new LivingWorldContext(
                Instant.parse("2026-06-27T12:00:00Z"),
                LocalDate.parse("2026-06-27"),
                WorldClockMode.DAILY,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(festival),
                Set.of()));

        assertThat(result.events()).hasSize(1);
        assertThat(result.trace()).anyMatch(entry -> entry.rule().equals("gossip-generated"));
    }

    private static LivingWorldProperties enabledProperties() {
        LivingWorldProperties properties = new LivingWorldProperties();
        properties.setGossipEnabled(true);
        return properties;
    }
}
