package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;

public record MemoryConsolidationReportResponseDto(
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
        String emailError) {}
