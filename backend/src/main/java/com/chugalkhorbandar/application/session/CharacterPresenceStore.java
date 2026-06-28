package com.chugalkhorbandar.application.session;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class CharacterPresenceStore {

    private final ConcurrentHashMap<String, Instant> lastSeenByCharacterId = new ConcurrentHashMap<>();

    public void recordSeen(String characterId, Instant when) {
        if (characterId == null || characterId.isBlank()) {
            return;
        }
        lastSeenByCharacterId.put(characterId, when);
    }

    public Optional<Instant> lastSeen(String characterId) {
        return Optional.ofNullable(lastSeenByCharacterId.get(characterId));
    }
}
