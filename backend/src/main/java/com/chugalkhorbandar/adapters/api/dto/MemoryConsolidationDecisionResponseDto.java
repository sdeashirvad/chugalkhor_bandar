package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationDecision;
import java.util.List;

public record MemoryConsolidationDecisionResponseDto(
        MemoryConsolidationDecision decision, String reason, List<String> inboxItemIds) {}
