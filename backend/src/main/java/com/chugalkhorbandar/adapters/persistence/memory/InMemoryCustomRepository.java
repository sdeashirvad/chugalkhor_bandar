package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;

import com.chugalkhorbandar.domain.world.ports.CustomRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCustom;
import java.util.List;
import java.util.Optional;

public final class InMemoryCustomRepository implements CustomRepository {

    private final InMemoryWorldStore store;

    public InMemoryCustomRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeCustom custom) {
        putUnique(store.customs(), custom.id(), custom, "Customs");
    }

    @Override
    public boolean exists(String customId) {
        return store.customs().containsKey(customId);
    }

    @Override
    public Optional<RuntimeCustom> findById(String customId) {
        return Optional.ofNullable(store.customs().get(customId));
    }

    @Override
    public List<RuntimeCustom> findAll() {
        return List.copyOf(store.customs().values());
    }
}
