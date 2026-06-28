package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;
import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.requirePresent;

import com.chugalkhorbandar.adapters.persistence.PersistenceException;
import com.chugalkhorbandar.domain.world.ports.TerritoryRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class InMemoryTerritoryRepository implements TerritoryRepository {

    private final InMemoryWorldStore store;

    public InMemoryTerritoryRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeTerritory territory) {
        putUnique(store.territories(), territory.id(), territory, "Territories");
    }

    @Override
    public boolean exists(String territoryId) {
        return store.territories().containsKey(territoryId);
    }

    @Override
    public Optional<RuntimeTerritory> findById(String territoryId) {
        return Optional.ofNullable(store.territories().get(territoryId));
    }

    @Override
    public List<RuntimeTerritory> findAll() {
        return List.copyOf(store.territories().values());
    }

    @Override
    public void changeRuler(String territoryId, String newRulerId) {
        RuntimeTerritory territory = requirePresent(store.territories(), territoryId, "Territories");
        store.territories().put(territoryId, territory.withCurrentRulerId(newRulerId));
    }

    @Override
    public void transferOwnership(String territoryId, String fromRulerId, String toRulerId) {
        RuntimeTerritory territory = requirePresent(store.territories(), territoryId, "Territories");
        if (!Objects.equals(territory.currentRulerId(), fromRulerId)) {
            throw new PersistenceException(
                    "Territory " + territoryId + " is not ruled by " + fromRulerId);
        }
        store.territories().put(territoryId, territory.withCurrentRulerId(toRulerId));
    }
}
