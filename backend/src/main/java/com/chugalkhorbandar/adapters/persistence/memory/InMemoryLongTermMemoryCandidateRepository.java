package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.memory.consolidation.LongTermMemoryCandidate;
import com.chugalkhorbandar.domain.memory.consolidation.ports.LongTermMemoryCandidateRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryLongTermMemoryCandidateRepository implements LongTermMemoryCandidateRepository {

    private final InMemoryMemoryConsolidationStore store;

    public InMemoryLongTermMemoryCandidateRepository(InMemoryMemoryConsolidationStore store) {
        this.store = store;
    }

    @Override
    public LongTermMemoryCandidate save(LongTermMemoryCandidate candidate) {
        return store.saveCandidate(candidate);
    }

    @Override
    public Optional<LongTermMemoryCandidate> findById(String id) {
        return store.findCandidateById(id);
    }

    @Override
    public List<LongTermMemoryCandidate> findByRunId(String runId) {
        return store.findCandidatesByRunId(runId);
    }

    @Override
    public List<LongTermMemoryCandidate> findAllOrderByCreatedAtDesc() {
        return store.findAllCandidates();
    }
}
