package com.chugalkhorbandar.application.chronicle;

import java.time.Instant;
import java.util.List;

public record ChronicleWriteResult(
        String runId,
        Instant startedAt,
        Instant completedAt,
        long durationMs,
        int candidatesProcessed,
        int chroniclesWritten,
        int skipped,
        List<Chronicle> chronicles,
        List<ChronicleWriteTraceEntry> trace) {

    public ChronicleWriteResult {
        chronicles = List.copyOf(chronicles == null ? List.of() : chronicles);
        trace = List.copyOf(trace == null ? List.of() : trace);
    }
}
