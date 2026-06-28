package com.chugalkhorbandar.application.memory.consolidation;

import java.util.List;

public record MemoryConsolidationResult(
        List<LongTermMemoryCandidate> candidates,
        List<MemoryConsolidationDecisionRecord> decisions,
        List<MemoryConsolidationTraceEntry> trace,
        MemoryConsolidationDailyStats dailyStats) {

    public MemoryConsolidationResult {
        candidates = List.copyOf(candidates == null ? List.of() : candidates);
        decisions = List.copyOf(decisions == null ? List.of() : decisions);
        trace = List.copyOf(trace == null ? List.of() : trace);
    }
}
