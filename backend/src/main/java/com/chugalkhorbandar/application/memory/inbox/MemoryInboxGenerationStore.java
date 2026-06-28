package com.chugalkhorbandar.application.memory.inbox;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class MemoryInboxGenerationStore {

    private final ConcurrentHashMap<String, MemoryInboxGenerationSnapshot> latestByCharacterId =
            new ConcurrentHashMap<>();

    public void save(MemoryInboxGenerationSnapshot snapshot) {
        latestByCharacterId.put(snapshot.characterId(), snapshot);
    }

    public Optional<MemoryInboxGenerationSnapshot> findByCharacterId(String characterId) {
        return Optional.ofNullable(latestByCharacterId.get(characterId));
    }
}
