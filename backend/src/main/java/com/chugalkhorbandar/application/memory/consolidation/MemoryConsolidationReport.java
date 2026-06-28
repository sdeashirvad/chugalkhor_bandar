package com.chugalkhorbandar.application.memory.consolidation;

import java.time.Instant;

public record MemoryConsolidationReport(
        String runId,
        Instant startedAt,
        Instant completedAt,
        long durationMs,
        int processed,
        int promoted,
        int discarded,
        int expired,
        int archived,
        int pending,
        int candidateCount,
        String summary,
        String txtReport,
        String jsonReport,
        String reflection,
        String emailStatus,
        String emailError) {

    public MemoryConsolidationReport {
        summary = summary == null ? "" : summary;
        txtReport = txtReport == null ? "" : txtReport;
        jsonReport = jsonReport == null ? "" : jsonReport;
        reflection = reflection == null ? "" : reflection;
        emailStatus = emailStatus == null ? "SKIPPED" : emailStatus;
        emailError = emailError == null ? "" : emailError;
    }
}
