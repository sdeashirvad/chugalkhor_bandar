package com.chugalkhorbandar.application.world.living;

import java.util.ArrayList;
import java.util.List;

public record LivingWorldGeneratorResult(
        List<WorldEvent> events,
        List<LivingWorldTraceEntry> trace) {

    public LivingWorldGeneratorResult {
        events = List.copyOf(events == null ? List.of() : events);
        trace = List.copyOf(trace == null ? List.of() : trace);
    }

    public static LivingWorldGeneratorResult empty() {
        return new LivingWorldGeneratorResult(List.of(), List.of());
    }

    public LivingWorldGeneratorResult merge(LivingWorldGeneratorResult other) {
        List<WorldEvent> mergedEvents = new ArrayList<>(events);
        mergedEvents.addAll(other.events());
        List<LivingWorldTraceEntry> mergedTrace = new ArrayList<>(trace);
        mergedTrace.addAll(other.trace());
        return new LivingWorldGeneratorResult(mergedEvents, mergedTrace);
    }
}
