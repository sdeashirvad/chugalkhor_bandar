package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import com.chugalkhorbandar.domain.memory.inbox.ports.MemoryInboxRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryMemoryInboxRepository implements MemoryInboxRepository {

    private final InMemoryMemoryInboxStore store;

    public InMemoryMemoryInboxRepository(InMemoryMemoryInboxStore store) {
        this.store = store;
    }

    @Override
    public MemoryInboxItem save(MemoryInboxItem item) {
        return store.save(item);
    }

    @Override
    public Optional<MemoryInboxItem> findById(String id) {
        return store.findById(id);
    }

    @Override
    public Optional<MemoryInboxItem> findByOwnerCharacterIdAndSourceAndSourceId(
            String ownerCharacterId, String source, String sourceId) {
        return store.findByOwnerCharacterIdAndSourceAndSourceId(ownerCharacterId, source, sourceId);
    }

    @Override
    public List<MemoryInboxItem> findByOwnerCharacterId(String ownerCharacterId) {
        return store.findByOwnerCharacterId(ownerCharacterId);
    }

    @Override
    public List<MemoryInboxItem> findAll() {
        return store.findAll();
    }
}
