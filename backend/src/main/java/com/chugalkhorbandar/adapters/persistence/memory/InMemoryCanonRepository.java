package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;

import com.chugalkhorbandar.domain.world.ports.CanonRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCanon;
import java.util.List;
import java.util.Optional;

public final class InMemoryCanonRepository implements CanonRepository {

    private final InMemoryWorldStore store;

    public InMemoryCanonRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeCanon canon) {
        putUnique(store.canon(), canon.id(), canon, "Canon");
    }

    @Override
    public boolean exists(String canonId) {
        return store.canon().containsKey(canonId);
    }

    @Override
    public Optional<RuntimeCanon> findById(String canonId) {
        return Optional.ofNullable(store.canon().get(canonId));
    }

    @Override
    public List<RuntimeCanon> findAll() {
        return List.copyOf(store.canon().values());
    }
}
