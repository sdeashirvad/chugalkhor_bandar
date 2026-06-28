package com.chugalkhorbandar.application.memory.consolidation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemoryConsolidationReportGeneratorTest {

    private MemoryConsolidationReportGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new MemoryConsolidationReportGenerator();
    }

    @Test
    void generatesDeterministicTxtAndJson() throws Exception {
        MemoryConsolidationDailyStats stats = new MemoryConsolidationDailyStats(
                "2026-06-01", 3, 5, 2, 1, 1, 1, 2, 1);
        MemoryConsolidationResult result = new MemoryConsolidationResult(
                List.of(new LongTermMemoryCandidate(
                        "c-1",
                        List.of("i-1"),
                        "character_alpha",
                        "Remember this",
                        com.chugalkhorbandar.application.memory.inbox.MemoryInboxImportance.HIGH,
                        "Consolidated from PROMISE",
                        java.time.Instant.parse("2026-06-01T12:00:00Z"),
                        "run-1",
                        Map.of("chronicleCandidate", "true"))),
                List.of(),
                List.of(),
                stats);

        String txt = generator.generateTxt(stats, result);
        String json = generator.generateJson(stats, result);

        assertThat(txt).contains("Jungle Daily Report");
        assertThat(txt).contains("Date: 2026-06-01");
        assertThat(txt).contains("Pending Promises: 2");
        assertThat(json).contains("\"date\" : \"2026-06-01\"");
        assertThat(json).contains("\"chronicleCandidates\"");
    }
}
