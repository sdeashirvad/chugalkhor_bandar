package com.chugalkhorbandar.domain.memory.ports;

import com.chugalkhorbandar.application.memory.working.WorkingMemorySnapshot;
import java.util.Optional;

public interface WorkingMemoryRepository {

    Optional<WorkingMemorySnapshot> findBySessionId(String sessionId);

    WorkingMemorySnapshot save(WorkingMemorySnapshot snapshot);

    void deleteBySessionId(String sessionId);
}
