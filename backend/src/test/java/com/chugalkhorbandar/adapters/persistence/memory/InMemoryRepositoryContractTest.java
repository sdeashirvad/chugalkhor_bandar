package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.adapters.persistence.RepositoryContractTestBase;
import com.chugalkhorbandar.domain.world.ports.WorldPersistenceService;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;

class InMemoryRepositoryContractTest extends RepositoryContractTestBase {

    private InMemoryWorldStore store;

    @Override
    protected WorldRepositoryProvider createProvider() {
        store = new InMemoryWorldStore();
        return new InMemoryWorldRepositoryProvider(store);
    }

    @Override
    protected WorldPersistenceService createPersistenceService(WorldRepositoryProvider provider) {
        return new InMemoryWorldPersistenceService(store, provider);
    }
}
