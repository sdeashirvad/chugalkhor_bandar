package com.chugalkhorbandar.application.memory.consolidation;

import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationResult;
import java.time.Instant;
import java.util.List;

public record MemoryConsolidationExecutionSnapshot(
        String runId,
        Instant startedAt,
        Instant completedAt,
        MemoryConsolidationResult result,
        MemoryConsolidationReport report,
        List<LongTermMemoryCandidate> candidates,
        String reflection,
        String emailStatus,
        String emailError) {}
