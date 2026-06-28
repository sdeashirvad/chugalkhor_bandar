package com.chugalkhorbandar.application.notification;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class NotificationGenerationStore {

    private final ConcurrentHashMap<String, NotificationGenerationSnapshot> latestByCharacter = new ConcurrentHashMap<>();

    public void save(NotificationGenerationSnapshot snapshot) {
        latestByCharacter.put(snapshot.characterId(), snapshot);
    }

    public Optional<NotificationGenerationSnapshot> findByCharacterId(String characterId) {
        return Optional.ofNullable(latestByCharacter.get(characterId));
    }
}
