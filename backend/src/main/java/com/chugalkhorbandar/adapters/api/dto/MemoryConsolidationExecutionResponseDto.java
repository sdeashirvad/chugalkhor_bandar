package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationDecision;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationTraceEntry;
import java.time.Instant;
import java.util.List;

public record MemoryConsolidationExecutionResponseDto(
        String runId,
        Instant startedAt,
        Instant completedAt,
        MemoryConsolidationReportResponseDto report,
        List<LongTermMemoryCandidateResponseDto> candidates,
        List<MemoryConsolidationTraceEntry> trace,
        List<MemoryConsolidationDecisionResponseDto> decisions,
        String reflection,
        String emailStatus,
        String emailError) {}
