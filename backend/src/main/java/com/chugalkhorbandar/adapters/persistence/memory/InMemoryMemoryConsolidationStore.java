package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.memory.consolidation.LongTermMemoryCandidate;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryMemoryConsolidationStore {

    private final ConcurrentHashMap<String, MemoryConsolidationReport> reportsByRunId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LongTermMemoryCandidate> candidatesById = new ConcurrentHashMap<>();

    public MemoryConsolidationReport saveReport(MemoryConsolidationReport report) {
        reportsByRunId.put(report.runId(), report);
        return report;
    }

    public Optional<MemoryConsolidationReport> findReportByRunId(String runId) {
        return Optional.ofNullable(reportsByRunId.get(runId));
    }

    public Optional<MemoryConsolidationReport> findLatestReport() {
        return reportsByRunId.values().stream()
                .max(Comparator.comparing(MemoryConsolidationReport::startedAt));
    }

    public List<MemoryConsolidationReport> findReportHistory() {
        return reportsByRunId.values().stream()
                .sorted(Comparator.comparing(MemoryConsolidationReport::startedAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public LongTermMemoryCandidate saveCandidate(LongTermMemoryCandidate candidate) {
        candidatesById.put(candidate.id(), candidate);
        return candidate;
    }

    public Optional<LongTermMemoryCandidate> findCandidateById(String id) {
        return Optional.ofNullable(candidatesById.get(id));
    }

    public List<LongTermMemoryCandidate> findCandidatesByRunId(String runId) {
        return candidatesById.values().stream()
                .filter(candidate -> runId.equals(candidate.runId()))
                .sorted(Comparator.comparing(LongTermMemoryCandidate::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<LongTermMemoryCandidate> findAllCandidates() {
        return candidatesById.values().stream()
                .sorted(Comparator.comparing(LongTermMemoryCandidate::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
