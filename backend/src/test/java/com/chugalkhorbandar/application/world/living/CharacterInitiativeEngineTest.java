package com.chugalkhorbandar.application.world.living;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactPriority;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
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

class CharacterInitiativeEngineTest {

    @Test
    void rabbituMinisterRequestsMeetingAfterRabbituChronicle() {
        CharacterInitiativeEngine engine = new CharacterInitiativeEngine(enabledProperties());
        Chronicle chronicle = sampleChronicle(
                "chron-1",
                ChronicleCategory.WORLD,
                "character_bandar",
                "Rabbitu politics shift");

        LivingWorldGeneratorResult result = engine.generate(context(List.of(chronicle), List.of(), List.of()));

        assertThat(result.events()).anyMatch(event -> event.title().contains("Rabbitu Minister"));
    }

    @Test
    void bandarContinuesUnfinishedStoryWhenActiveStorySeedExists() {
        CharacterInitiativeEngine engine = new CharacterInitiativeEngine(enabledProperties());
        ConversationArtifact storySeed = new ConversationArtifact(
                "story-1",
                ConversationArtifactType.STORY_SEED,
                "character_bandar",
                "character_alpha",
                "character_alpha",
                "conv-1",
                "Lost Crown",
                "Unfinished tale",
                ConversationArtifactStatus.ACTIVE,
                ConversationArtifactPriority.MEDIUM,
                Instant.parse("2026-06-01T00:00:00Z"),
                Instant.parse("2026-07-01T00:00:00Z"),
                Instant.parse("2026-06-01T00:00:00Z"),
                Map.of(),
                List.of());

        LivingWorldGeneratorResult result = engine.generate(context(List.of(), List.of(storySeed), List.of(storySeed)));

        assertThat(result.events()).anyMatch(event -> event.title().contains("Bandar continues"));
    }

    private static LivingWorldProperties enabledProperties() {
        LivingWorldProperties properties = new LivingWorldProperties();
        properties.setCharacterInitiativeEnabled(true);
        return properties;
    }

    private static LivingWorldContext context(
            List<Chronicle> chronicles,
            List<ConversationArtifact> activeArtifacts,
            List<ConversationArtifact> allArtifacts) {
        Instant now = Instant.parse("2026-06-27T12:00:00Z");
        LocalDate today = LocalDate.parse("2026-06-27");
        return new LivingWorldContext(
                now,
                today,
                WorldClockMode.DAILY,
                List.of(),
                activeArtifacts,
                allArtifacts,
                chronicles,
                List.of(),
                Set.of());
    }

    private static Chronicle sampleChronicle(
            String id, ChronicleCategory category, String ownerCharacterId, String summary) {
        Instant now = Instant.parse("2026-06-27T12:00:00Z");
        return new Chronicle(
                id,
                "Chronicle",
                category,
                ChronicleVisibility.PUBLIC,
                ChronicleConfidence.OFFICIAL,
                ownerCharacterId,
                summary,
                summary,
                now,
                LocalDate.parse("2026-06-27"),
                Map.of(),
                new ChronicleProvenance("", List.of(), List.of(), List.of(), "", "", id, List.of(), Map.of()),
                1);
    }
}
