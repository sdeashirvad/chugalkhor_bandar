package com.chugalkhorbandar.application.behavior;

import java.util.List;

public record BehaviorPlanningTrace(List<BehaviorPlanningTraceEntry> entries) {

    public BehaviorPlanningTrace {
        entries = List.copyOf(entries == null ? List.of() : entries);
    }
}
