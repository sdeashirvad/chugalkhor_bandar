package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.WorldStatePersister;
import com.chugalkhorbandar.domain.world.ports.WorldPersistenceService;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.WorldUnitOfWork;
import com.chugalkhorbandar.domain.world.runtime.WorldRuntime;
import org.springframework.transaction.PlatformTransactionManager;

public final class PostgresWorldPersistenceService implements WorldPersistenceService {

    private final WorldRepositoryProvider provider;
    private final PlatformTransactionManager transactionManager;

    public PostgresWorldPersistenceService(
            WorldRepositoryProvider provider, PlatformTransactionManager transactionManager) {
        this.provider = provider;
        this.transactionManager = transactionManager;
    }

    @Override
    public WorldUnitOfWork beginUnitOfWork() {
        return new JpaWorldUnitOfWork(transactionManager);
    }

    @Override
    public void persist(WorldRuntime runtime, WorldUnitOfWork unitOfWork) {
        WorldStatePersister.persist(provider, runtime.state());
    }
}
