package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;
import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.requirePresent;

import com.chugalkhorbandar.domain.world.ports.WorldRulesRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeWorldRules;
import java.util.List;
import java.util.Optional;

public final class InMemoryWorldRulesRepository implements WorldRulesRepository {

    private final InMemoryWorldStore store;

    public InMemoryWorldRulesRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeWorldRules worldRules) {
        putUnique(store.worldRules(), worldRules.id(), worldRules, "WorldRules");
    }

    @Override
    public void update(RuntimeWorldRules worldRules) {
        requirePresent(store.worldRules(), worldRules.id(), "WorldRules");
        store.worldRules().put(worldRules.id(), worldRules);
    }

    @Override
    public boolean exists(String worldRulesId) {
        return store.worldRules().containsKey(worldRulesId);
    }

    @Override
    public Optional<RuntimeWorldRules> findById(String worldRulesId) {
        return Optional.ofNullable(store.worldRules().get(worldRulesId));
    }

    @Override
    public List<RuntimeWorldRules> findAll() {
        return List.copyOf(store.worldRules().values());
    }
}
