package com.chugalkhorbandar.application.memory.consolidation;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import java.util.List;

public record MemoryConsolidationDecisionRecord(
        MemoryConsolidationDecision decision,
        List<MemoryInboxItem> inboxItems,
        String reason,
        LongTermMemoryCandidate candidate) {

    public MemoryConsolidationDecisionRecord {
        inboxItems = List.copyOf(inboxItems == null ? List.of() : inboxItems);
    }
}
