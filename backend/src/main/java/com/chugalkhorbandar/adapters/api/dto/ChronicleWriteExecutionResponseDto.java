package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;
import java.util.List;

public record ChronicleWriteExecutionResponseDto(
        String runId,
        Instant startedAt,
        Instant completedAt,
        long durationMs,
        int candidatesProcessed,
        int chroniclesWritten,
        int skipped,
        List<ChronicleResponseDto> chronicles,
        List<ChronicleWriteTraceEntryResponseDto> trace) {}
