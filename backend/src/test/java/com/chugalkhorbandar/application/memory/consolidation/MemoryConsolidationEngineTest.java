package com.chugalkhorbandar.application.memory.consolidation;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxSource;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemoryConsolidationEngineTest {

    private MemoryConsolidationEngine engine;
    private Instant now;

    @BeforeEach
    void setUp() {
        MemoryConsolidationProperties properties = new MemoryConsolidationProperties();
        engine = new MemoryConsolidationEngine(properties);
        now = Instant.parse("2026-06-01T12:00:00Z");
    }

    @Test
    void promotesPromiseItems() {
        MemoryConsolidationResult result = engine.consolidate(input(List.of(
                inboxItem("i-1", "PROMISE", "Remember this", 0.9, MemoryInboxStatus.NEW))));

        assertThat(result.candidates()).hasSize(1);
        assertThat(result.decisions()).anyMatch(record -> record.decision() == MemoryConsolidationDecision.PROMOTE);
    }

    @Test
    void promotesRepeatedPreferences() {
        MemoryConsolidationResult result = engine.consolidate(input(List.of(
                inboxItem("i-1", "PREFERENCE", "Likes tea", 0.8, MemoryInboxStatus.NEW),
                inboxItem("i-2", "PREFERENCE", "Likes tea", 0.85, MemoryInboxStatus.REVIEWED))));

        assertThat(result.candidates()).hasSize(1);
    }

    @Test
    void discardsSingleLowConfidenceObservation() {
        MemoryConsolidationResult result = engine.consolidate(input(List.of(
                inboxItem("i-1", "PREFERENCE", "Maybe likes tea", 0.2, MemoryInboxStatus.NEW))));

        assertThat(result.candidates()).isEmpty();
        assertThat(result.decisions()).anyMatch(record -> record.decision() == MemoryConsolidationDecision.DISCARD);
    }

    @Test
    void discardsUnknownObservations() {
        MemoryConsolidationResult result = engine.consolidate(input(List.of(
                inboxItem("i-1", "UNKNOWN", "Unclear", 0.9, MemoryInboxStatus.NEW))));

        assertThat(result.candidates()).isEmpty();
        assertThat(result.decisions()).anyMatch(record -> record.decision() == MemoryConsolidationDecision.DISCARD);
    }

    @Test
    void ignoresAlreadyPromotedAndDiscardedItems() {
        MemoryConsolidationResult result = engine.consolidate(input(List.of(
                inboxItem("i-1", "PROMISE", "Old promise", 0.9, MemoryInboxStatus.PROMOTED),
                inboxItem("i-2", "PROMISE", "Discarded promise", 0.9, MemoryInboxStatus.DISCARDED))));

        assertThat(result.candidates()).isEmpty();
    }

    @Test
    void groupsRelatedItemsBySummary() {
        MemoryConsolidationResult result = engine.consolidate(input(List.of(
                inboxItem("i-1", "STORY_SEED", "Jungle tale", 0.7, MemoryInboxStatus.NEW),
                inboxItem("i-2", "STORY_SEED", "Jungle tale", 0.75, MemoryInboxStatus.NEW))));

        assertThat(result.candidates()).hasSize(1);
        assertThat(result.candidates().getFirst().sourceInboxItems()).containsExactlyInAnyOrder("i-1", "i-2");
    }

    private MemoryConsolidationEngineInput input(List<MemoryInboxItem> items) {
        return new MemoryConsolidationEngineInput(
                items,
                "status=READY",
                LocalDate.parse("2026-06-01"),
                now,
                new MemoryConsolidationDailyStats("2026-06-01", 1, 2, items.size(), 0, 0, 0, 1, 0));
    }

    private static MemoryInboxItem inboxItem(
            String id, String type, String summary, double confidence, MemoryInboxStatus status) {
        Instant created = Instant.parse("2026-06-01T10:00:00Z");
        return new MemoryInboxItem(
                id,
                type,
                MemoryInboxSource.COGNITIVE_OBSERVATION,
                "source-" + id,
                "character_alpha",
                summary,
                MemoryInboxImportance.MEDIUM,
                confidence,
                status,
                created,
                created.plus(30, ChronoUnit.DAYS),
                Map.of("conversationId", "conv-1"),
                List.of("created"),
                "",
                List.of());
    }
}
