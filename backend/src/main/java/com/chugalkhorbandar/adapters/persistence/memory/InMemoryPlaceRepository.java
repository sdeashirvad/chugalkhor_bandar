package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;

import com.chugalkhorbandar.domain.world.ports.PlaceRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import java.util.List;
import java.util.Optional;

public final class InMemoryPlaceRepository implements PlaceRepository {

    private final InMemoryWorldStore store;

    public InMemoryPlaceRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimePlace place) {
        putUnique(store.places(), place.id(), place, "Places");
    }

    @Override
    public boolean exists(String placeId) {
        return store.places().containsKey(placeId);
    }

    @Override
    public Optional<RuntimePlace> findById(String placeId) {
        return Optional.ofNullable(store.places().get(placeId));
    }

    @Override
    public List<RuntimePlace> findAll() {
        return List.copyOf(store.places().values());
    }
}
