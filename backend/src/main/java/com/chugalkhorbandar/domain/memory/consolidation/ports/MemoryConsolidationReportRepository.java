package com.chugalkhorbandar.domain.memory.consolidation.ports;

import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import java.util.List;
import java.util.Optional;

public interface MemoryConsolidationReportRepository {

    MemoryConsolidationReport save(MemoryConsolidationReport report);

    Optional<MemoryConsolidationReport> findByRunId(String runId);

    Optional<MemoryConsolidationReport> findLatest();

    List<MemoryConsolidationReport> findHistoryOrderByStartedAtDesc();
}
