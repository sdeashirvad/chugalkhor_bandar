package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;
import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.requirePresent;

import com.chugalkhorbandar.adapters.persistence.PersistenceException;
import com.chugalkhorbandar.domain.world.ports.ObjectRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeObject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class InMemoryObjectRepository implements ObjectRepository {

    private final InMemoryWorldStore store;

    public InMemoryObjectRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeObject object) {
        putUnique(store.objects(), object.id(), object, "Objects");
    }

    @Override
    public boolean exists(String objectId) {
        return store.objects().containsKey(objectId);
    }

    @Override
    public Optional<RuntimeObject> findById(String objectId) {
        return Optional.ofNullable(store.objects().get(objectId));
    }

    @Override
    public List<RuntimeObject> findByOwner(String ownerId) {
        return store.objects().values().stream()
                .filter(object -> Objects.equals(object.ownerId(), ownerId))
                .toList();
    }

    @Override
    public void transferOwnership(String objectId, String fromOwnerId, String toOwnerId) {
        RuntimeObject object = requirePresent(store.objects(), objectId, "Objects");
        if (!Objects.equals(object.ownerId(), fromOwnerId)) {
            throw new PersistenceException("Object " + objectId + " is not owned by " + fromOwnerId);
        }
        store.objects().put(objectId, object.withOwnerId(toOwnerId));
    }
}
