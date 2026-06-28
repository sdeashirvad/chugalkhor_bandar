package com.chugalkhorbandar.domain.memory.inbox.ports;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import java.util.List;
import java.util.Optional;

public interface MemoryInboxRepository {

    MemoryInboxItem save(MemoryInboxItem item);

    Optional<MemoryInboxItem> findById(String id);

    Optional<MemoryInboxItem> findByOwnerCharacterIdAndSourceAndSourceId(
            String ownerCharacterId, String source, String sourceId);

    List<MemoryInboxItem> findByOwnerCharacterId(String ownerCharacterId);

    List<MemoryInboxItem> findAll();
}
