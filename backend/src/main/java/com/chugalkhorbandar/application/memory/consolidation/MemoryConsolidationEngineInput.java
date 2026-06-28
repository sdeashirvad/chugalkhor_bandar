package com.chugalkhorbandar.application.memory.consolidation;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record MemoryConsolidationEngineInput(
        List<MemoryInboxItem> inboxItems,
        String runtimeWorldSummary,
        LocalDate currentDate,
        Instant currentTime,
        MemoryConsolidationDailyStats dailyStats) {

    public MemoryConsolidationEngineInput {
        inboxItems = List.copyOf(inboxItems == null ? List.of() : inboxItems);
        runtimeWorldSummary = runtimeWorldSummary == null ? "" : runtimeWorldSummary;
    }
}
