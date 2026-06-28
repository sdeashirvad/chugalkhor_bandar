package com.chugalkhorbandar.domain.memory.consolidation.ports;

import com.chugalkhorbandar.application.memory.consolidation.LongTermMemoryCandidate;
import java.util.List;
import java.util.Optional;

public interface LongTermMemoryCandidateRepository {

    LongTermMemoryCandidate save(LongTermMemoryCandidate candidate);

    Optional<LongTermMemoryCandidate> findById(String id);

    List<LongTermMemoryCandidate> findByRunId(String runId);

    List<LongTermMemoryCandidate> findAllOrderByCreatedAtDesc();
}
