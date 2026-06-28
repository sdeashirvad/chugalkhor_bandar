package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.memory.inbox.MemoryInboxItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryMemoryInboxStore {

    private final ConcurrentHashMap<String, MemoryInboxItem> itemsById = new ConcurrentHashMap<>();

    public MemoryInboxItem save(MemoryInboxItem item) {
        itemsById.put(item.id(), item);
        return item;
    }

    public Optional<MemoryInboxItem> findById(String id) {
        return Optional.ofNullable(itemsById.get(id));
    }

    public Optional<MemoryInboxItem> findByOwnerCharacterIdAndSourceAndSourceId(
            String ownerCharacterId, String source, String sourceId) {
        return itemsById.values().stream()
                .filter(item -> ownerCharacterId.equals(item.ownerCharacterId()))
                .filter(item -> source.equals(item.source().name()))
                .filter(item -> sourceId.equals(item.sourceId()))
                .findFirst();
    }

    public List<MemoryInboxItem> findByOwnerCharacterId(String ownerCharacterId) {
        return itemsById.values().stream()
                .filter(item -> ownerCharacterId.equals(item.ownerCharacterId()))
                .sorted(Comparator.comparing(MemoryInboxItem::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<MemoryInboxItem> findAll() {
        return itemsById.values().stream()
                .sorted(Comparator.comparing(MemoryInboxItem::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
