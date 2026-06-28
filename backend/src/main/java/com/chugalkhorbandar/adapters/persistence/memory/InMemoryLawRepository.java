package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;

import com.chugalkhorbandar.domain.world.ports.LawRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeLaw;
import java.util.List;
import java.util.Optional;

public final class InMemoryLawRepository implements LawRepository {

    private final InMemoryWorldStore store;

    public InMemoryLawRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeLaw law) {
        putUnique(store.laws(), law.id(), law, "Laws");
    }

    @Override
    public boolean exists(String lawId) {
        return store.laws().containsKey(lawId);
    }

    @Override
    public Optional<RuntimeLaw> findById(String lawId) {
        return Optional.ofNullable(store.laws().get(lawId));
    }

    @Override
    public List<RuntimeLaw> findAll() {
        return List.copyOf(store.laws().values());
    }
}
