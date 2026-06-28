package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import com.chugalkhorbandar.domain.memory.consolidation.ports.MemoryConsolidationReportRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryMemoryConsolidationReportRepository implements MemoryConsolidationReportRepository {

    private final InMemoryMemoryConsolidationStore store;

    public InMemoryMemoryConsolidationReportRepository(InMemoryMemoryConsolidationStore store) {
        this.store = store;
    }

    @Override
    public MemoryConsolidationReport save(MemoryConsolidationReport report) {
        return store.saveReport(report);
    }

    @Override
    public Optional<MemoryConsolidationReport> findByRunId(String runId) {
        return store.findReportByRunId(runId);
    }

    @Override
    public Optional<MemoryConsolidationReport> findLatest() {
        return store.findLatestReport();
    }

    @Override
    public List<MemoryConsolidationReport> findHistoryOrderByStartedAtDesc() {
        return store.findReportHistory();
    }
}
