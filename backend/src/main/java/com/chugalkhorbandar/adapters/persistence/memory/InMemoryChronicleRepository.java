package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.chronicle.Chronicle;
import com.chugalkhorbandar.application.chronicle.ChronicleCategory;
import com.chugalkhorbandar.application.chronicle.ChronicleVisibility;
import com.chugalkhorbandar.domain.chronicle.ports.ChronicleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryChronicleRepository implements ChronicleRepository {

    private final InMemoryChronicleStore store;

    public InMemoryChronicleRepository(InMemoryChronicleStore store) {
        this.store = store;
    }

    @Override
    public Chronicle save(Chronicle chronicle) {
        return store.save(chronicle);
    }

    @Override
    public Optional<Chronicle> findById(String id) {
        return store.findById(id);
    }

    @Override
    public Optional<Chronicle> findLatestByCandidateId(String candidateId) {
        return store.findLatestByCandidateId(candidateId);
    }

    @Override
    public int countByCandidateId(String candidateId) {
        return store.countByCandidateId(candidateId);
    }

    @Override
    public List<Chronicle> findAllOrderByCreatedAtDesc() {
        return store.findAll();
    }

    @Override
    public List<Chronicle> findByCategoryOrderByCreatedAtDesc(ChronicleCategory category) {
        return store.findByCategory(category);
    }

    @Override
    public List<Chronicle> findByVisibilityOrderByCreatedAtDesc(ChronicleVisibility visibility) {
        return store.findByVisibility(visibility);
    }
}
