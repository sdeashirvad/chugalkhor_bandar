package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.memory.working.WorkingMemorySnapshot;
import com.chugalkhorbandar.domain.memory.ports.WorkingMemoryRepository;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryWorkingMemoryRepository implements WorkingMemoryRepository {

    private final InMemoryWorkingMemoryStore store;

    public InMemoryWorkingMemoryRepository(InMemoryWorkingMemoryStore store) {
        this.store = store;
    }

    @Override
    public Optional<WorkingMemorySnapshot> findBySessionId(String sessionId) {
        return store.findBySessionId(sessionId);
    }

    @Override
    public WorkingMemorySnapshot save(WorkingMemorySnapshot snapshot) {
        return store.save(snapshot);
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        store.deleteBySessionId(sessionId);
    }
}
