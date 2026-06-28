package com.chugalkhorbandar.application.memory.working;

import java.util.List;

public record WorkingMemorySnapshot(WorkingMemory memory, List<WorkingMemoryFieldTrace> fieldTraces) {

    public WorkingMemorySnapshot {
        fieldTraces = List.copyOf(fieldTraces == null ? List.of() : fieldTraces);
    }
}
