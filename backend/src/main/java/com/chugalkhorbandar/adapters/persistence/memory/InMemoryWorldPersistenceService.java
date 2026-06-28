package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.adapters.persistence.WorldStatePersister;
import com.chugalkhorbandar.domain.world.ports.WorldPersistenceService;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.WorldUnitOfWork;
import com.chugalkhorbandar.domain.world.runtime.WorldRuntime;

public final class InMemoryWorldPersistenceService implements WorldPersistenceService {

    private final InMemoryWorldStore store;
    private final WorldRepositoryProvider provider;

    public InMemoryWorldPersistenceService(InMemoryWorldStore store, WorldRepositoryProvider provider) {
        this.store = store;
        this.provider = provider;
    }

    @Override
    public WorldUnitOfWork beginUnitOfWork() {
        return new InMemoryWorldUnitOfWork(store);
    }

    @Override
    public void persist(WorldRuntime runtime, WorldUnitOfWork unitOfWork) {
        WorldStatePersister.persist(provider, runtime.state());
    }
}
