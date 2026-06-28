package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;
import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.requirePresent;

import com.chugalkhorbandar.domain.world.ports.ResourceRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeResource;
import java.util.List;
import java.util.Optional;

public final class InMemoryResourceRepository implements ResourceRepository {

    private final InMemoryWorldStore store;

    public InMemoryResourceRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeResource resource) {
        putUnique(store.resources(), resource.id(), resource, "Resources");
    }

    @Override
    public boolean exists(String resourceId) {
        return store.resources().containsKey(resourceId);
    }

    @Override
    public Optional<RuntimeResource> findById(String resourceId) {
        return Optional.ofNullable(store.resources().get(resourceId));
    }

    @Override
    public List<RuntimeResource> findAll() {
        return List.copyOf(store.resources().values());
    }

    @Override
    public void consume(String resourceId, String consumerId, int quantity) {
        RuntimeResource resource = requirePresent(store.resources(), resourceId, "Resources");
        int remaining = resource.availableQuantity() - quantity;
        store.resources().put(resourceId, resource.withAvailableQuantity(Math.max(0, remaining)));
    }
}
