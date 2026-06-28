package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.domain.world.ports.WorldUnitOfWork;

public final class InMemoryWorldUnitOfWork implements WorldUnitOfWork {

    private final InMemoryWorldStore store;
    private InMemoryWorldStore.Snapshot snapshot;
    private boolean active;

    public InMemoryWorldUnitOfWork(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void begin() {
        snapshot = store.snapshot();
        active = true;
    }

    @Override
    public void commit() {
        snapshot = null;
        active = false;
    }

    @Override
    public void rollback() {
        if (active && snapshot != null) {
            store.restore(snapshot);
        }
        snapshot = null;
        active = false;
    }
}
